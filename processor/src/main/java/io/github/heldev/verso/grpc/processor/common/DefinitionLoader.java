package io.github.heldev.verso.grpc.processor.common;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import io.github.heldev.verso.grpc.processor.common.type.ProtoType;
import io.github.heldev.verso.grpc.processor.common.type.ProtoTypeBasic;
import io.github.heldev.verso.grpc.processor.common.type.ProtoTypeDeclared;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.github.heldev.verso.grpc.processor.common.Utils.optionalWhen;
import static java.util.stream.Collectors.toList;

public class DefinitionLoader {
	private final Supplier<FileDescriptorSet> descriptorSetSource;

	public DefinitionLoader(
			Supplier<FileDescriptorSet> descriptorSetSource) {
		this.descriptorSetSource = descriptorSetSource;
	}

	public DefinitionCatalog loadDefinitions() {
		List<MessageDefinition> definitions = descriptorSetSource.get()
				.getFileList().stream()
				.flatMap(this::extractMessages)
				.collect(toList());

		return DefinitionCatalog.of(definitions);
	}

	private MessageDefinitionContext buildContext(FileDescriptorProto file) {
		return MessageDefinitionContext.builder()
				.protoFilepath(file.getName())
				.protoPackage(optionalWhen(file.hasPackage(), file.getPackage()))
				.isMultipleFiles(file.getOptions().getJavaMultipleFiles())
				.javaPackage(optionalWhen(file.getOptions().hasJavaPackage(), file.getOptions().getJavaPackage()))
				.outerClassName(optionalWhen(file.getOptions().hasJavaOuterClassname(), file.getOptions().getJavaOuterClassname()))
				.build();
	}

	private Stream<MessageDefinition> extractMessages(FileDescriptorProto file) {
		MessageDefinitionContext context = buildContext(file);
		return file.getMessageTypeList().stream()
				.map(message -> extractMessage(context, message));
	}

	private MessageDefinition extractMessage(
			MessageDefinitionContext context,
			DescriptorProto message) {
		return MessageDefinition.builder()
				.context(context)
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
				.protoType(toProtoType(field))
				.isOptional(field.getProto3Optional())
				.build();
	}

	private ProtoType toProtoType(FieldDescriptorProto field) {
		switch (field.getType()) {
			case TYPE_BOOL:
				return ProtoTypeBasic.BOOLEAN;
			case TYPE_INT32:
			case TYPE_UINT32:
			case TYPE_SINT32:
			case TYPE_FIXED32:
			case TYPE_SFIXED32:
				return ProtoTypeBasic.INT;
			case TYPE_INT64:
			case TYPE_UINT64:
			case TYPE_SINT64:
			case TYPE_FIXED64:
			case TYPE_SFIXED64:
				return ProtoTypeBasic.LONG;
			case TYPE_FLOAT:
				return ProtoTypeBasic.FLOAT;
			case TYPE_DOUBLE:
				return ProtoTypeBasic.DOUBLE;
			case TYPE_BYTES:
				return ProtoTypeBasic.BYTES;
			case TYPE_STRING:
				return ProtoTypeBasic.STRING;
			case TYPE_MESSAGE:
				return ProtoTypeDeclared.message(getQualifiedName(field.getTypeName()));
			case TYPE_ENUM:
				return ProtoTypeDeclared.enumType(getQualifiedName(field.getTypeName()));
			case TYPE_GROUP:
			default:
				throw new RuntimeException(field + " type is not supported");
		}
	}

	private String getQualifiedName(String field) {
		return field.replaceFirst("^\\.", "");
	}

}
