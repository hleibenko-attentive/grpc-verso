package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.interfaces.VersoField;
import io.github.heldev.verso.grpc.interfaces.VersoGenerated;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.processor.Generator;
import io.github.heldev.verso.grpc.processor.GeneratorCatalog;
import io.github.heldev.verso.grpc.processor.common.DefinitionCatalog;
import io.github.heldev.verso.grpc.processor.common.MessageDefinition;
import io.github.heldev.verso.grpc.processor.common.MessageField;
import io.github.heldev.verso.grpc.processor.common.NamingConventions;
import io.github.heldev.verso.grpc.processor.common.type.ProtoType;
import io.github.heldev.verso.grpc.processor.common.type.ProtoTypeBasic;
import io.github.heldev.verso.grpc.processor.common.type.ProtoTypeDeclared;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Optional;

import static io.github.heldev.verso.grpc.processor.common.Utils.panic;
import static io.github.heldev.verso.grpc.processor.prototranslation.OptionalAttributeType.OPTIONAL_SETTER;
import static io.github.heldev.verso.grpc.processor.prototranslation.OptionalAttributeType.VALUE_AND_OPTIONAL_SETTERS;
import static io.github.heldev.verso.grpc.processor.prototranslation.OptionalAttributeType.VALUE_SETTER;
import static java.lang.Character.toLowerCase;
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

	public TargetType buildTargetType(GeneratorCatalog generatorCatalog, TypeElement typeElement) {
		ExecutableElement builderFactoryMethod = getBuilderFactoryMethod(typeElement);
		MessageDefinition messageType = getMessageType(typeElement)
				.orElseThrow(() -> panic("can't resolve proto definition for " + typeElement));

		return TargetType.builder()
				.type(typeElement.asType())
				.protoMessage(messageType.qualifiedClass())
				.builderType(builderFactoryMethod.getReturnType())
				.builderFactoryMethod(builderFactoryMethod.getSimpleName())
				.attributes(buildAttributes(messageType, typeElement))
				.generatedAttributes(buildGeneratedAttributes(generatorCatalog, typeElement))
				.build();
	}

	private Optional<MessageDefinition> getMessageType(TypeElement typeElement) {
		String qualifiedName = typeElement.getAnnotation(VersoMessage.class).value();
		return definitionCatalog.findByName(qualifiedName);
	}

	private List<TargetField> buildAttributes(MessageDefinition messageType, TypeElement type) {

		return methodsIn(type.getEnclosedElements()).stream()
//				.filter(method -> method.getModifiers().stream().noneMatch(modifier -> EnumSet.of(PRIVATE, STATIC).contains(modifier)))
//				.filter(method -> method.getParameters().isEmpty())
				.filter(method -> method.getAnnotation(VersoField.class) != null)
				.map(method -> buildAttribute(messageType, type, method))
				.collect(toList());
	}

	private TargetField buildAttribute(
			MessageDefinition messageType,
			TypeElement type,
			ExecutableElement attributeAccessor) {
		MessageField field = findMessageFieldById(messageType, attributeAccessor)
				.orElseThrow(() -> panic(
						"can't find field in " + messageType + " matching " + attributeAccessor));

		String protobufGetterSuffix = NamingConventions.getProtobufGetterSuffix(field.name());

		TargetField.Builder builder = TargetField.builder()
				.attribute(getAttributeName(attributeAccessor))
				.type(attributeAccessor.getReturnType())
				.protobufGetter("get" + protobufGetterSuffix)
				.protobufType(toJavaType(field.protoType()));

		if (field.isOptional()) {
			builder.presenceCheckingMethod("has" + protobufGetterSuffix);
			builder.optionalAttributeType(getOptionalAttributeType(getBuilderSetters(type, attributeAccessor)));
		}
		return builder.build();
	}

	private String getAttributeName(ExecutableElement attributeAccessor) {
		String accessorName = attributeAccessor.getSimpleName().toString();
		return accessorName.matches("^get\\p{javaUpperCase}.*")
				? toLowerCase(accessorName.charAt(3)) + accessorName.substring(4)
				: accessorName;
	}

	private TypeMirror toJavaType(ProtoType type) {
		return type.isBasic()
				? toJavaType((ProtoTypeBasic) type)
				: toJavaType((ProtoTypeDeclared) type);
	}

	private TypeMirror toJavaType(ProtoTypeBasic type) {
		switch (type) {
			case BOOLEAN:
				return typeUtils.getPrimitiveType(TypeKind.BOOLEAN);
			case INT:
				return typeUtils.getPrimitiveType(TypeKind.INT);
			case LONG:
				return typeUtils.getPrimitiveType(TypeKind.LONG);
			case FLOAT:
				return typeUtils.getPrimitiveType(TypeKind.FLOAT);
			case DOUBLE:
				return typeUtils.getPrimitiveType(TypeKind.DOUBLE);
			case BYTES:
				return typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.BYTE));
			case STRING:
				return typeUtils.getDeclaredType(elementUtils.getTypeElement(String.class.getCanonicalName()));
			default:
				throw panic("please report bug: unsupported type %s" + type);
		}
	}

	private TypeMirror toJavaType(ProtoTypeDeclared type) {
		if (! type.isEnum()) {
			String qualifiedClass = definitionCatalog.findByName(type.qualifiedName())
					.orElseThrow(() -> panic("can't resolve " + type))
					.qualifiedClass();
			return elementUtils.getTypeElement(qualifiedClass).asType();
		} else {
			throw panic("enums are not supported yet " + type);
		}
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
			MessageDefinition messageType,
			ExecutableElement method) {

		int fieldId = method.getAnnotation(VersoField.class).value();
		return messageType.findFieldById(fieldId);
	}

	private ExecutableElement getBuilderFactoryMethod(TypeElement typeElement) {
		return methodsIn(typeElement.getEnclosedElements()).stream()
				.filter(method -> method.getModifiers().contains(STATIC))
				.filter(method -> method.getParameters().isEmpty())
				.filter(method -> method.getSimpleName().contentEquals("builder"))
				.findAny()
				.orElseThrow(() -> panic("builder method is missing"));
	}

	private List<GeneratedAttribute> buildGeneratedAttributes(
			GeneratorCatalog generatorCatalog,
			TypeElement type) {
		return methodsIn(type.getEnclosedElements()).stream()
				.filter(method -> method.getAnnotation(VersoGenerated.class) != null)
				.map(attributeAccessor -> buildGeneratedAttribute(generatorCatalog, attributeAccessor))
				.collect(toList());
	}

	private GeneratedAttribute buildGeneratedAttribute(
			GeneratorCatalog generatorCatalog,
			ExecutableElement attributeAccessor) {

		return GeneratedAttribute.builder()
				.name(getAttributeName(attributeAccessor))
				.generator(getGenerator(generatorCatalog, attributeAccessor))
				.build();
	}

	private Generator getGenerator(GeneratorCatalog generatorCatalog, ExecutableElement attributeAccessor) {
		String generatorName = attributeAccessor.getAnnotation(VersoGenerated.class).value();

		return generatorCatalog.findByName(generatorName)
				.orElseThrow(() -> panic("can't find a generator for " + attributeAccessor));
	}
}
