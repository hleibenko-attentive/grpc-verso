package io.github.heldev.verso.grpc.processor;

import com.squareup.javapoet.JavaFile;
import io.github.heldev.verso.grpc.interfaces.VersoCustomTranslator;
import io.github.heldev.verso.grpc.interfaces.VersoCustomGenerator;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.processor.common.DefinitionAdapter;
import io.github.heldev.verso.grpc.processor.common.DescriptorSetFilepathSource;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetConverterRenderer;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTranslatorsTranslator;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTranslatorsViewModel;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetType;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTypeTranslator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.tools.Diagnostic.Kind.ERROR;

public class VersoProcessor extends AbstractProcessor {
	private TargetConverterRenderer targetConverterRenderer;
	private TranslatorTranslator translatorTranslator;
	private GeneratorTranslator generatorTranslator;
	private TargetTranslatorsTranslator targetTranslatorsTranslator;
	private TargetTypeTranslator targetTypeTranslator;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		translatorTranslator = new TranslatorTranslator();
		generatorTranslator = new GeneratorTranslator();
		targetConverterRenderer = new TargetConverterRenderer();

		DefinitionAdapter definitionAdapter = new DefinitionAdapter(
				new DescriptorSetFilepathSource(Paths.get(getDescriptorSetPath(processingEnv))));

		targetTypeTranslator = new TargetTypeTranslator(
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils(),
				//todo lazy supplier, parsing doesn't seem to be the best activity for DI context initialization
				definitionAdapter.get());

		targetTranslatorsTranslator = new TargetTranslatorsTranslator(
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils());
	}

	private String getDescriptorSetPath(ProcessingEnvironment processingEnv) {
		return processingEnv.getOptions()
				.getOrDefault("verso.descriptorSetPath", "/tmp/grpc-verso/descriptorset.protobin");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		generateTranslators(roundEnv,
				translatorTranslator.loadFieldTranslators(roundEnv),
				generatorTranslator.loadGenerators(roundEnv));
		return true;
	}

	private void generateTranslators(
			RoundEnvironment roundEnv,
			TranslatorCatalog translatorCatalog,
			GeneratorCatalog generatorCatalog) {

		typesIn(roundEnv.getElementsAnnotatedWith(VersoMessage.class))
				.forEach(type -> {
					TargetType targetType = targetTypeTranslator.buildTargetType(generatorCatalog, type);
					TargetTranslatorsViewModel translatorView = targetTranslatorsTranslator.translate(translatorCatalog, targetType);
					JavaFile file = targetConverterRenderer.render(translatorView);
					try {
						file.writeTo(processingEnv.getFiler());
					} catch (IOException e) {
						StringWriter stringWriter = new StringWriter();
						e.printStackTrace(new PrintWriter(stringWriter));
						processingEnv.getMessager().printMessage(ERROR, stringWriter.toString());
					}
				});
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(VersoCustomTranslator.class, VersoMessage.class, VersoCustomGenerator.class)
				.map(Class::getCanonicalName)
				.collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
}
