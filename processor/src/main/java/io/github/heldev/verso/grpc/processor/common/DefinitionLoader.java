package io.github.heldev.verso.grpc.processor.common;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DefinitionLoader {

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
				.type(field.getType().toString())
				.build();
	}
}
