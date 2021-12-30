package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.interfaces.VersoField;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.processor.common.DefinitionCatalog;
import io.github.heldev.verso.grpc.processor.common.MessageField;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.heldev.verso.grpc.processor.prototranslation.OptionalAttributeType.OPTIONAL_SETTER;
import static io.github.heldev.verso.grpc.processor.prototranslation.OptionalAttributeType.VALUE_AND_OPTIONAL_SETTERS;
import static io.github.heldev.verso.grpc.processor.prototranslation.OptionalAttributeType.VALUE_SETTER;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.methodsIn;

public class TargetTypeTranslator {
	private final Types typeUtils;
	private final Elements elementUtils;
	private final DefinitionCatalog definitionCatalog;

	public TargetTypeTranslator(
			Types typeUtils,
			Elements elementsUtils,
			DefinitionCatalog definitionCatalog) {
		this.typeUtils = typeUtils;
		this.elementUtils = elementsUtils;
		this.definitionCatalog = definitionCatalog;
	}

	public TargetType buildTargetType(TypeElement typeElement) {
		ExecutableElement builderFactoryMethod = getBuilderFactoryMethod(typeElement);
		String messageQualifiedName = typeElement.getAnnotation(VersoMessage.class).value();

		return TargetType.builder()
				.type(typeElement.asType())
				.protoMessage(messageQualifiedName)
				.builderType(builderFactoryMethod.getReturnType())
				.builderFactoryMethod(builderFactoryMethod.getSimpleName())
				.attributes(buildAttributes(messageQualifiedName, typeElement))
				.build();
	}

	private List<TargetField> buildAttributes(String messageQualifiedName, TypeElement type) {

		return methodsIn(type.getEnclosedElements()).stream()
//				.filter(method -> method.getModifiers().stream().noneMatch(modifier -> EnumSet.of(PRIVATE, STATIC).contains(modifier)))
//				.filter(method -> method.getParameters().isEmpty())
				.filter(method -> method.getAnnotation(VersoField.class) != null)
				.map(method -> buildAttribute(messageQualifiedName, type, method))
				.collect(toList());
	}

	private TargetField buildAttribute(
			String messageQualifiedName,
			TypeElement type,
			ExecutableElement attributeGetter) {
		MessageField field = findMessageFieldById(messageQualifiedName, attributeGetter)
				.orElseThrow(() -> new RuntimeException(
						"can't find field in " + messageQualifiedName + " matching " + attributeGetter));

		String protobufGetterSuffix = getProtobufGetterSuffix(field.name());

		TargetField.Builder builder = TargetField.builder()
				.getter(attributeGetter.getSimpleName().toString())
				.type(attributeGetter.getReturnType())
				.protobufGetter("get" + protobufGetterSuffix)
				.protobufType(field.type());

		if (field.isOptional()) {
			builder.presenceCheckingMethod("has" + protobufGetterSuffix);
			builder.optionalAttributeType(getOptionalAttributeType(getBuilderSetters(type, attributeGetter)));
		}
		return builder.build();
	}

	private OptionalAttributeType getOptionalAttributeType(List<ExecutableElement> builderSetters) {
		if (containsOptionalArgumentSetter(builderSetters)) {
			return builderSetters.size() > 1 ? VALUE_AND_OPTIONAL_SETTERS : OPTIONAL_SETTER;
		} else {
			return VALUE_SETTER;
		}
	}

	private boolean containsOptionalArgumentSetter(List<ExecutableElement> builderSetters) {
		return builderSetters.stream()
				.anyMatch(setter -> setter.getParameters().stream().allMatch(this::isOptional));
	}

	private boolean isOptional(VariableElement parameter) {
		DeclaredType rawOptionalType = typeUtils.getDeclaredType(
				elementUtils.getTypeElement(Optional.class.getCanonicalName()));

		return typeUtils.isAssignable(parameter.asType(), rawOptionalType);
	}

	private List<ExecutableElement> getBuilderSetters(TypeElement type, ExecutableElement attributeGetter) {
		return getMethods(type)
				.stream()
				.filter(method -> method.getSimpleName().equals(attributeGetter.getSimpleName()))
				.filter(method -> ! method.getModifiers().contains(PRIVATE))
				.filter(method -> method.getParameters().size() == 1)
				.collect(toList());
	}

	private List<ExecutableElement> getMethods(TypeElement type) {
		TypeMirror builderType = getBuilderFactoryMethod(type).getReturnType();
		return methodsIn(typeUtils.asElement(builderType).getEnclosedElements());
	}

	private Optional<MessageField> findMessageFieldById(
			String messageQualifiedName,
			ExecutableElement method) {

		int fieldId = method.getAnnotation(VersoField.class).value();
		return definitionCatalog.findFieldByMessageAndId(messageQualifiedName, fieldId);
	}

	/**
	 * <a href="// https://github.com/protocolbuffers/protobuf/blob/master/src/google/protobuf/compiler/java/java_helpers.cc"
	 * 		>proto conversion algorithm</a>
	 */
	private String getProtobufGetterSuffix(String protoFieldName) {

		//todo check digits
		return Stream.of(protoFieldName.split("_"))
				.map(this::capitalize)
				 .collect(joining());
	}

	private String capitalize(String s) {
		return IntStream.concat(s.chars().limit(1).map(this::latinToUpperCase), s.chars().skip(1))
				.collect(StringBuilder::new, (builder, c) -> builder.append((char) c), StringBuilder::append)
				.toString();
	}

	private char latinToUpperCase(int c) {
		int codePoint = 'a' <= c && c <= 'z' ? Character.toUpperCase(c) : c;
		return (char) codePoint;
	}

	private ExecutableElement getBuilderFactoryMethod(TypeElement typeElement) {
		return methodsIn(typeElement.getEnclosedElements()).stream()
				.filter(method -> method.getModifiers().contains(STATIC))
				.filter(method -> method.getParameters().isEmpty())
				.filter(method -> method.getSimpleName().contentEquals("builder"))
				.findAny()
				.orElseThrow(() -> new RuntimeException("builder method is missing"));
	}
}
