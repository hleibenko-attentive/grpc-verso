package io.github.heldev.verso.grpc.processor.prototranslation;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.github.heldev.verso.grpc.interfaces.VersoField;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.heldev.verso.grpc.processor.Unit.UNIT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.methodsIn;

public class TargetTypeTranslatorInstanceBased {
	private final Types typeUtils;
	private final Elements elementsUtils;

	public TargetTypeTranslatorInstanceBased(Types typeUtils, Elements elementsUtils) {
		this.typeUtils = typeUtils;
		this.elementsUtils = elementsUtils;
	}

	public TargetType buildTargetType(TypeElement typeElement) {
		ExecutableElement builderFactoryMethod = getBuilderFactoryMethod(typeElement);

		return TargetType.builder()
				.type(typeElement.asType())
				.builderType(builderFactoryMethod.getReturnType())
				.builderFactoryMethod(builderFactoryMethod.getSimpleName())
				.fields(buildFields(typeElement))
				.build();
	}

	private List<TargetField> buildFields(TypeElement typeElement) {
		Optional<? extends AnnotationValue> value = typeElement.getAnnotationMirrors().stream()
				.filter(annotation -> annotation.getAnnotationType().asElement().getSimpleName().contentEquals(VersoMessage.class.getSimpleName()))
				.findAny()
				.flatMap(messageAnnotation -> messageAnnotation.getElementValues()
						.entrySet().stream()
						.filter(entry -> entry.getKey().getSimpleName().contentEquals("value"))
						.map(Map.Entry::getValue)
						.findAny());


		Descriptors.Descriptor messageDescriptor = value
				.map(v -> (TypeMirror) v.getValue())
				.map(type -> (TypeElement) typeUtils.asElement(type))
				.map(v -> {
					try {
						return getMessageInstance((Class<? extends GeneratedMessageV3>) Class.forName(v.getQualifiedName().toString()));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}).orElseThrow(() -> new RuntimeException("can't get proto message for " + typeElement))
				.getDescriptorForType();

		return methodsIn(typeElement.getEnclosedElements()).stream()
//				.filter(method -> method.getModifiers().stream().noneMatch(modifier -> EnumSet.of(PRIVATE, STATIC).contains(modifier)))
//				.filter(method -> method.getParameters().isEmpty())
				.filter(method -> method.getAnnotation(VersoField.class) != null)
				.map(method -> buildField(messageDescriptor, method))
				.collect(toList());
	}

	private TargetField buildField(Descriptors.Descriptor messageDescriptor, ExecutableElement method) {
		return TargetField.builder()
				.getter(method.getSimpleName().toString())
				.type(method.getReturnType())
				.protobufGetter(getProtobufGetter(messageDescriptor, method).toString())
				.build();
	}

	private CharSequence getProtobufGetter(Descriptors.Descriptor messageDescriptor, ExecutableElement method) {
		int fieldId = method.getAnnotation(VersoField.class).value();
		return Optional.ofNullable(messageDescriptor.findFieldByNumber(fieldId))
				.map(field -> getJavaGetterName(field.getName()))
				.orElseThrow(() -> new RuntimeException(messageDescriptor + " has no field id " + fieldId));
	}


	private String getJavaGetterName(String protoFieldName) {
		// https://github.com/protocolbuffers/protobuf/blob/master/src/google/protobuf/compiler/java/java_helpers.cc
		//todo check digits
		return "get" + Stream.of(protoFieldName.split("_")).map(this::capitalize).collect(joining());
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

	private Message getMessageInstance(Class<? extends GeneratedMessageV3> messageClass) {
		try {
			return (Message) messageClass
					.getDeclaredMethod("getDefaultInstanceForType")
					.invoke(UNIT);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
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
