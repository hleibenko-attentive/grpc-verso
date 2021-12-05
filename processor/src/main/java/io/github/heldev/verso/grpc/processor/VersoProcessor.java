package io.github.heldev.verso.grpc.processor;

import com.squareup.javapoet.JavaFile;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.interfaces.VersoFieldTranslator;
import io.github.heldev.verso.grpc.processor.common.DefinitionLoader;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetConverterRenderer;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetField;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTranslatorViewModel;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetType;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTypeTranslator;
import io.github.heldev.verso.grpc.processor.prototranslation.field.FieldSource;
import io.github.heldev.verso.grpc.processor.prototranslation.field.GetterFieldSource;
import io.github.heldev.verso.grpc.processor.prototranslation.field.TranslatorFieldSource;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.tools.Diagnostic.Kind.ERROR;

public class VersoProcessor extends AbstractProcessor {
	private TargetTypeTranslator targetTypeTranslator;
	private TargetConverterRenderer targetConverterRenderer;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		targetTypeTranslator = new TargetTypeTranslator(
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils(),
				new DefinitionLoader().loadDefinitions());

		targetConverterRenderer = new TargetConverterRenderer();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		typesIn(roundEnv.getElementsAnnotatedWith(VersoMessage.class))
				.forEach(type -> {
					JavaFile file = buildPrimitiveTranslator(type);

					try {
						file.writeTo(processingEnv.getFiler());
					} catch (IOException e) {
						processingEnv.getMessager().printMessage(ERROR, e.toString());
					}
				});

		return true;
	}

	private JavaFile buildPrimitiveTranslator(TypeElement type) {
		TargetType targetType = targetTypeTranslator.buildTargetType(type);

		TargetTranslatorViewModel viewModel = TargetTranslatorViewModel.builder()
				.javaPackage(targetType.javaPackage())
				.name(processingEnv.getTypeUtils().asElement(targetType.type()).getSimpleName() + "Translator")
				.targetBuilderType(targetType.builderType())
				.targetType(targetType.type())
				//todo null check
				.sourceType(processingEnv.getElementUtils().getTypeElement(targetType.protoMessage()).asType())
				.fieldSources(buildFieldSource(targetType))
				.build();

		return targetConverterRenderer.render(viewModel);
	}

	private Map<String, FieldSource> buildFieldSource(TargetType targetType) {
		return targetType.fields().stream()
				.collect(toMap(
						TargetField::getter,
						field -> {
							if (field.getter().equals("uuid")) {
								return TranslatorFieldSource.builder()
										.translatorClass(processingEnv.getElementUtils().getTypeElement("io.github.heldev.verso.grpc.app.CustomConverters").asType())
										.method("stringToUuid")
										.underlyingSource(GetterFieldSource.of(field.protobufGetter()))
										.build();
							} else {
								return GetterFieldSource.of(field.protobufGetter());
							}
						}));
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
