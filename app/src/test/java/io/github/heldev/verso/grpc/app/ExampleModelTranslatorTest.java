package io.github.heldev.verso.grpc.app;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleModelTranslatorTest {

	@Test
	public void populates_builder_from_proto() {
		ExampleMessage message = ExampleMessage.newBuilder()
				.setExampleString("string")
				.setExampleInt64(64)
				.build();

		ExampleModel.Builder result = ExampleModelTranslator.toBuilder(message);
		ExampleModel.Builder expected = ExampleModel.builder()
				.string("string")
				.int64(64);

		assertThat(fillMissingDataAndBuild(result))
				.isEqualTo(fillMissingDataAndBuild(expected));
	}

	private ExampleModel fillMissingDataAndBuild(ExampleModel.Builder builder) {
		return builder.uuid(UUID.fromString("0-0-0-0-0")).build();
	}
}
