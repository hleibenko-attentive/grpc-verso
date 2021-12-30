package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.processor.TranslatorCatalog;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.heldev.verso.grpc.processor.prototranslation.OptionalAttributeType.VALUE_SETTER;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;

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

		Map<Boolean, List<AttributeViewModel>> attributesByOptionalAttributeWithOnlyValueSettersStatus = targetType.attributes()
				.stream()
				.collect(partitioningBy(
						attribute -> attribute.optionalAttributeType().equals(Optional.of(VALUE_SETTER)),
						mapping(attribute -> buildView(translatorCatalog, attribute), toList())));

		return TargetTranslatorsViewModel.builder()
				.javaPackage(targetType.javaPackage())
				.name(typeUtils.asElement(targetType.type()).getSimpleName() + "Translators")
				.targetBuilderType(targetType.builderType())
				.targetType(targetType.type())
				//todo null check
				.sourceType(elementUtils.getTypeElement(targetType.protoMessage()).asType())
				.optionalAttributesWithNonOptionalArguments(attributesByOptionalAttributeWithOnlyValueSettersStatus.getOrDefault(true, emptyList()))
				.otherAttributes(attributesByOptionalAttributeWithOnlyValueSettersStatus.getOrDefault(false, emptyList()))
				.build();
	}

	private AttributeViewModel buildView(
			TranslatorCatalog translatorCatalog,
			TargetField field) {

		return AttributeViewModel.builder()
				.builderSetter(field.getter()) //todo getter prefix
				.sourceGetter(field.protobufGetter())
				.sourcePresenceCheckingMethod(field.presenceCheckingMethod())
				.translator(findTranslator(translatorCatalog, field).map(this::toViewModel))
				.build();
	}

	private Optional<Translator> findTranslator(
			TranslatorCatalog translatorCatalog,
			TargetField field) {
		TypeMirror from = field.protobufType();
		TypeMirror to = unwrapOptionalType(field);

		return typeUtils.isSameType(from, to)
				? Optional.empty()
				: Optional.of(getTranslator(translatorCatalog, from, to));
	}

	private TypeMirror unwrapOptionalType(TargetField field) {
		TypeMirror type = field.type();

		return isOptional(type)
				? ((DeclaredType) type).getTypeArguments().get(0) //todo handle raw types
				: type;
	}

	private boolean isOptional(TypeMirror type) {
		DeclaredType rawOptionalType = typeUtils.getDeclaredType(
				elementUtils.getTypeElement(Optional.class.getCanonicalName()));

		return typeUtils.isAssignable(type, rawOptionalType);
	}

	private Translator getTranslator(
			TranslatorCatalog translatorCatalog,
			TypeMirror from,
			TypeMirror to) {
		return translatorCatalog
				.findTranslator(typeUtils, from, to)
				.orElseThrow(() -> new RuntimeException("can't find translator for " + from + " to " + to));

	}

	private TranslatorViewModel toViewModel(
			Translator translator) {

		return TranslatorViewModel.builder()
				.clazz(translator.location())
				.method(translator.method())
				.build();
	}
}
