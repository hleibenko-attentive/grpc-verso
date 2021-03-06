package io.github.heldev.verso.grpc.app;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
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
				.setOptionalString("optionalString")
				.setIsoDateTime("2007-12-03T10:15:30")
				.setTimestamp(Timestamp.newBuilder().setSeconds(100).setNanos(5).build())
				.setOptionalUuid(uuid.toString())
				.build();

		ExampleModel.Builder result = ExampleModelTranslators.toBuilder(message);
		ExampleModel.Builder expected = ExampleModel.builder()
				.string("string")
				.stringWithGetterPrefix("string")
				.int64(64)
				.uuid(uuid)
				.date(LocalDate.parse("2007-12-03"))
				.time(LocalTime.parse("10:15:30"))
				.optionalString(Optional.of("optionalString"))
				.optionalUuid(uuid)
				.optionalOfNonOptionalUuid(UUID.fromString("dd62f4c3-c337-42e5-9491-85ba4ef0b0dc"))
				.instant(Instant.ofEpochSecond(100, 5));

		assertThat(fillMissingDataAndBuild(result))
				.isEqualTo(fillMissingDataAndBuild(expected));
	}

	private ExampleModel fillMissingDataAndBuild(ExampleModel.Builder builder) {
		return builder.build();
	}
}
