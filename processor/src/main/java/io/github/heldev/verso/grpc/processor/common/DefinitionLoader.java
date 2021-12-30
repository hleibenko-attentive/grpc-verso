package io.github.heldev.verso.grpc.processor.common;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DefinitionLoader {
	private final Types typeUtils;
	private final Elements elementUtils;

	public DefinitionLoader(Types typeUtils, Elements elementUtils) {
		this.typeUtils = typeUtils;
		this.elementUtils = elementUtils;
	}

	public DefinitionCatalog loadDefinitions() {
		List<MessageDefinition> messageDefinitions = load()
				.getFileList().stream()
				.flatMap(this::extractMessages)
				.collect(toList());

		return DefinitionCatalog.of(messageDefinitions);
	}

	private FileDescriptorSet load() {
		Path path = Paths.get("/tmp/grpc-verso/descriptors.protobin");
		try {
			return FileDescriptorSet.parseFrom(Files.readAllBytes(path));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Stream<MessageDefinition> extractMessages(FileDescriptorProto file) {
		return file.getMessageTypeList().stream()
				.map(message -> extractMessage(file, message));
	}

	private MessageDefinition extractMessage(FileDescriptorProto file, DescriptorProto message) {
		return MessageDefinition.builder()
				.protoPackage(file.getPackage())
				.protoFile(file.getName())
				.javaPackage(file.getOptions().getJavaPackage())
				.name(message.getName())
				.fields(extractFields(message.getFieldList()))
				.build();
	}

	private List<MessageField> extractFields(List<FieldDescriptorProto> fields) {
		return fields.stream()
				.map(this::extractField)
				.collect(toList());
	}

	private MessageField extractField(FieldDescriptorProto field) {
		return MessageField.builder()
				.id(field.getNumber())
				.name(field.getName())
				.type(getJavaTypeFromProtobufType(field))
				.isOptional(field.getProto3Optional())
				.build();
	}

	private TypeMirror getJavaTypeFromProtobufType(FieldDescriptorProto field) {
		switch (field.getType()) {
			case TYPE_DOUBLE:
				return typeUtils.getPrimitiveType(TypeKind.DOUBLE);
			case TYPE_FLOAT:
				return typeUtils.getPrimitiveType(TypeKind.FLOAT);
			case TYPE_INT64:
			case TYPE_UINT64:
				return typeUtils.getPrimitiveType(TypeKind.LONG);
			case TYPE_INT32:
			case TYPE_UINT32:
				return typeUtils.getPrimitiveType(TypeKind.INT);
			case TYPE_BOOL:
				return typeUtils.getPrimitiveType(TypeKind.BOOLEAN);
			case TYPE_STRING:
				return elementUtils.getTypeElement(String.class.getCanonicalName()).asType();
			case TYPE_MESSAGE:
				return elementUtils.getTypeElement(field.getTypeName().replaceFirst("^\\.", "com.")).asType();
			case TYPE_BYTES:
				return typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.BYTE));
			case TYPE_FIXED64:
			case TYPE_FIXED32:
			case TYPE_GROUP:
			case TYPE_ENUM:
			case TYPE_SFIXED32:
			case TYPE_SFIXED64:
			case TYPE_SINT32:
			case TYPE_SINT64:
			default:
				throw new RuntimeException(field + " type is not supported yet");
		}
	}
}
