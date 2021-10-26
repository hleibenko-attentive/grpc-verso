package io.github.heldev.verso.grpc.processor;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		try {
			FileDescriptorSet fileDescriptorSet = load();
			System.out.println(fileDescriptorSet);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private FileDescriptorSet load() throws IOException {
		return FileDescriptorSet.parseFrom(getClass().getClassLoader().getResourceAsStream("fds.pb"));
	}
}
