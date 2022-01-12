package io.github.heldev.verso.grpc.app;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleModel2TranslatorsTest {

	@Test
	public void builds_instance_from_proto() {
		int longValue = 64;
		UUID uuid = UUID.fromString("dd62f4c3-c337-42e5-9491-85ba4ef0b0dc");

		ExampleMessage message = ExampleMessage.newBuilder()
				.setExampleInt64(longValue)
				.setExampleUuid(uuid.toString())
				.build();

		assertThat(ExampleModel2Translators.fromMessage(message))
				.isEqualTo(ExampleModel2.builder()
						.int64(longValue)
						.uuid(uuid)
						.generatedUuid(Customizations.hardcodedUuid())
						.build());
	}
}
