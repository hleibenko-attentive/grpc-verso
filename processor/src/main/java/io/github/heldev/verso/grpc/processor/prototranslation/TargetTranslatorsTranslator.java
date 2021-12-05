package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.processor.TranslatorCatalog;
import io.github.heldev.verso.grpc.processor.prototranslation.field.FieldSource;
import io.github.heldev.verso.grpc.processor.prototranslation.field.GetterFieldSource;
import io.github.heldev.verso.grpc.processor.prototranslation.field.TranslatorFieldSource;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class TargetTranslatorsTranslator {
	private final TargetTypeTranslator targetTypeTranslator;
	private final Types typeUtils;
	private final Elements elementUtils;

	public TargetTranslatorsTranslator(
			TargetTypeTranslator targetTypeTranslator,
			Types typeUtils,
			Elements elementUtils) {
		this.targetTypeTranslator = targetTypeTranslator;
		this.typeUtils = typeUtils;
		this.elementUtils = elementUtils;
	}

	public TargetTranslatorsViewModel translate(TranslatorCatalog translatorCatalog, TypeElement type) {
		TargetType targetType = targetTypeTranslator.buildTargetType(type);

		return TargetTranslatorsViewModel.builder()
				.javaPackage(targetType.javaPackage())
				.name(typeUtils.asElement(targetType.type()).getSimpleName() + "Translators")
				.targetBuilderType(targetType.builderType())
				.targetType(targetType.type())
				//todo null check
				.sourceType(elementUtils.getTypeElement(targetType.protoMessage()).asType())
				.fieldSources(buildFieldSource(translatorCatalog, targetType))
				.build();

	}

	private Map<String, FieldSource> buildFieldSource(TranslatorCatalog translatorCatalog, TargetType targetType) {
		return targetType.fields().stream()
				.collect(toMap(
						TargetField::getter,
						field -> {
							if (field.getter().equals("uuid")) {
								return TranslatorFieldSource.builder()
										.translator(getTranslator(translatorCatalog, targetType, field))
										.underlyingSource(GetterFieldSource.of(field.protobufGetter()))
										.build();
							} else {
								return GetterFieldSource.of(field.protobufGetter());
							}
						}));
	}

	private Translator getTranslator(
			TranslatorCatalog translatorCatalog,
			TargetType targetType,
			TargetField field) {
		TypeMirror from = elementUtils.getTypeElement(String.class.getCanonicalName()).asType();

		return translatorCatalog
				.findTranslator(typeUtils, from, field.type())
				.orElseThrow(() -> new RuntimeException("can't find translator for " + targetType));
	}
}
