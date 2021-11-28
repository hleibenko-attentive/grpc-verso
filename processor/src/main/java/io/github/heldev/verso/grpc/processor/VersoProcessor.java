package io.github.heldev.verso.grpc.processor;

import com.squareup.javapoet.JavaFile;
import io.github.heldev.verso.grpc.interfaces.VersoClass;
import io.github.heldev.verso.grpc.processor.common.DefinitionLoader;
import io.github.heldev.verso.grpc.processor.prototranslation.Generator;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetConverterRenderer;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTranslatorViewModel;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.tools.Diagnostic.Kind.ERROR;

public class VersoProcessor extends AbstractProcessor {

	private Generator generator;
	private TargetConverterRenderer targetConverterRenderer;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		generator = new Generator(new DefinitionLoader(), new TargetConverterRenderer());
		targetConverterRenderer = new TargetConverterRenderer();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		JavaFile file = processT();

		if (isFirstRound(roundEnv)) {
			try {
				file.writeTo(processingEnv.getFiler());
			} catch (IOException e) {
				processingEnv.getMessager().printMessage(ERROR, e.toString());
			}
		}

		return true;
	}

	private boolean isFirstRound(RoundEnvironment roundEnv) {
		return roundEnv.getRootElements().size() > 1;
	}

	private JavaFile processT() {
		Map<String, String> propertySources = Stream.of(
						new SimpleEntry<>("string", "getExampleString"),
						new SimpleEntry<>("int64", "getExampleInt64")
				).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		TargetTranslatorViewModel viewModel = TargetTranslatorViewModel.builder()
				.javaPackage("io.testme")
				.targetBuilderType(processingEnv.getElementUtils().getTypeElement("io.github.heldev.verso.grpc.app.ExampleModel.Builder").asType())
				.targetType(processingEnv.getElementUtils().getTypeElement("io.github.heldev.verso.grpc.app.ExampleModel").asType())
				.sourceType(processingEnv.getElementUtils().getTypeElement("io.github.heldev.verso.grpc.app.ExampleMessage").asType())
				.fieldSources(propertySources)
				.name("ExampleModelTranslator")
				.build();

		return targetConverterRenderer.render(viewModel);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(VersoClass.class).map(Class::getCanonicalName).collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
}
