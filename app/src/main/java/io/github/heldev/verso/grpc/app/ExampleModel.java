package io.github.heldev.verso.grpc.app;

import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.interfaces.VersoField;
import org.immutables.value.Value;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_INT64_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_STRING_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_UUID_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.ISO_DATE_TIME_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.OPTIONAL_STRING_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.OPTIONAL_UUID_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.TIMESTAMP_FIELD_NUMBER;

@VersoMessage("io.github.heldev.grpcverso.ExampleMessage")
@Value.Immutable
public interface ExampleModel {

	static Builder builder() {
		return ImmutableExampleModel.builder();
	}

	@VersoField(EXAMPLE_STRING_FIELD_NUMBER)
	String string();

	@VersoField(EXAMPLE_STRING_FIELD_NUMBER)
	String getStringWithGetterPrefix();

	@VersoField(EXAMPLE_INT64_FIELD_NUMBER)
	long int64();

	@VersoField(EXAMPLE_UUID_FIELD_NUMBER)
	UUID uuid();

	@VersoField(OPTIONAL_STRING_FIELD_NUMBER)
	Optional<String> optionalString();

	@VersoField(OPTIONAL_UUID_FIELD_NUMBER)
	Optional<UUID> optionalUuid();

	@VersoField(EXAMPLE_UUID_FIELD_NUMBER)
	Optional<UUID> optionalOfNonOptionalUuid();

	@VersoField(ISO_DATE_TIME_FIELD_NUMBER)
	LocalDate date();

	@VersoField(ISO_DATE_TIME_FIELD_NUMBER)
	LocalTime time();

	@VersoField(TIMESTAMP_FIELD_NUMBER)
	Instant instant();

	interface Builder {
		Builder string(String string);
		Builder stringWithGetterPrefix(String string);
		Builder int64(long int64);
		Builder uuid(UUID uuid);
		Builder optionalString(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<String> string);
		Builder optionalUuid(UUID uuid);
		Builder optionalOfNonOptionalUuid(UUID uuid);
		Builder date(LocalDate date);
		Builder time(LocalTime time);
		Builder instant(Instant instant);

		ExampleModel build();
	}
}
