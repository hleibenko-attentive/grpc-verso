package io.github.heldev.verso.grpc.app;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleModelTranslatorsTest {

	@Test
	public void populates_builder_from_proto() {
		UUID uuid = UUID.fromString("dd62f4c3-c337-42e5-9491-85ba4ef0b0dc");

		ExampleMessage message = ExampleMessage.newBuilder()
				.setExampleString("string")
				.setExampleInt64(64)
				.setExampleUuid(uuid.toString())
				.build();

		ExampleModel.Builder result = ExampleModelTranslators.toBuilder(message);
		ExampleModel.Builder expected = ExampleModel.builder()
				.string("string")
				.int64(64)
				.uuid(uuid);

		assertThat(fillMissingDataAndBuild(result))
				.isEqualTo(fillMissingDataAndBuild(expected));
	}

	private ExampleModel fillMissingDataAndBuild(ExampleModel.Builder builder) {
		return builder.build();
	}
}
