package io.github.heldev.verso.grpc.processor;

import com.squareup.javapoet.JavaFile;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.interfaces.VersoFieldTranslator;
import io.github.heldev.verso.grpc.processor.common.DefinitionLoader;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetConverterRenderer;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTranslatorsTranslator;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTypeTranslator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.tools.Diagnostic.Kind.ERROR;

public class VersoProcessor extends AbstractProcessor {
	private TargetConverterRenderer targetConverterRenderer;
	private TranslatorTranslator translatorTranslator;
	private TargetTranslatorsTranslator targetTranslatorsTranslator;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		translatorTranslator = new TranslatorTranslator(processingEnv.getElementUtils());
		targetConverterRenderer = new TargetConverterRenderer();

		TargetTypeTranslator targetTypeTranslator = new TargetTypeTranslator(
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils(),
				new DefinitionLoader().loadDefinitions());

		targetTranslatorsTranslator = new TargetTranslatorsTranslator(
				targetTypeTranslator,
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		generateTranslators(roundEnv, translatorTranslator.loadFieldTranslators(roundEnv));
		return true;
	}

	private void generateTranslators(RoundEnvironment roundEnv, TranslatorCatalog translatorCatalog) {
		typesIn(roundEnv.getElementsAnnotatedWith(VersoMessage.class))
				.forEach(type -> {
					JavaFile file = targetConverterRenderer.render(targetTranslatorsTranslator.translate(translatorCatalog, type));
					try {
						file.writeTo(processingEnv.getFiler());
					} catch (IOException e) {
						processingEnv.getMessager().printMessage(ERROR, e.toString());
					}
				});
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(VersoFieldTranslator.class, VersoMessage.class)
				.map(Class::getCanonicalName)
				.collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
}
