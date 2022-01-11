package io.github.heldev.verso.grpc.processor.common;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class FileDescriptorSetSource implements Supplier<FileDescriptorSet> {
	private final Path descriptorSetLocation;

	public FileDescriptorSetSource(Path descriptorSetLocation) {
		this.descriptorSetLocation = descriptorSetLocation;
	}

	public FileDescriptorSet get() {
		try {
			return FileDescriptorSet.parseFrom(Files.readAllBytes(descriptorSetLocation));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
