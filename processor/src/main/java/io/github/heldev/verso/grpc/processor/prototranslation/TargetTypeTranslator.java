package io.github.heldev.verso.grpc.processor.prototranslation;

import io.github.heldev.verso.grpc.interfaces.VersoField;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.processor.common.DefinitionCatalog;
import io.github.heldev.verso.grpc.processor.common.MessageField;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.methodsIn;

public class TargetTypeTranslator {
	private final Types typeUtils;
	private final Elements elementsUtils;
	private final DefinitionCatalog definitionCatalog;

	public TargetTypeTranslator(
			Types typeUtils,
			Elements elementsUtils,
			DefinitionCatalog definitionCatalog) {
		this.typeUtils = typeUtils;
		this.elementsUtils = elementsUtils;
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
				.fields(buildFields(messageQualifiedName, typeElement))
				.build();
	}

	private List<TargetField> buildFields(String messageQualifiedName, TypeElement typeElement) {

		return methodsIn(typeElement.getEnclosedElements()).stream()
//				.filter(method -> method.getModifiers().stream().noneMatch(modifier -> EnumSet.of(PRIVATE, STATIC).contains(modifier)))
//				.filter(method -> method.getParameters().isEmpty())
				.filter(method -> method.getAnnotation(VersoField.class) != null)
				.map(method -> buildField(messageQualifiedName, method))
				.collect(toList());
	}

	private TargetField buildField(String messageQualifiedName, ExecutableElement method) {
		MessageField field = findMessageFieldById(messageQualifiedName, method)
				.orElseThrow(() -> new RuntimeException(
						"can't find field in " + messageQualifiedName + " matching " + method));

		return TargetField.builder()
				.getter(method.getSimpleName().toString())
				.type(method.getReturnType())
				.protobufGetter(getProtobufGetter(field.name()))
				.protobufType(field.type())
				.build();
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
	private String getProtobufGetter(String protoFieldName) {

		//todo check digits
		return "get" + Stream.of(protoFieldName.split("_"))
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
