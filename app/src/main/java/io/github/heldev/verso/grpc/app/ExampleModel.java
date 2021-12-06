package io.github.heldev.verso.grpc.app;

import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.interfaces.VersoField;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_INT64_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_STRING_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_UUID_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.ISO_DATE_TIME_FIELD_NUMBER;

@VersoMessage("io.github.heldev.verso.grpc.app.ExampleMessage")
@Value.Immutable
public abstract class ExampleModel {

	public static Builder builder() {
		return ImmutableExampleModel.builder();
	}

	@VersoField(EXAMPLE_STRING_FIELD_NUMBER)
	public abstract String string();

	@VersoField(EXAMPLE_INT64_FIELD_NUMBER)
	public abstract long int64();

	@VersoField(EXAMPLE_UUID_FIELD_NUMBER)
	public abstract UUID uuid();

	@VersoField(ISO_DATE_TIME_FIELD_NUMBER)
	public abstract LocalDate date();

	@VersoField(ISO_DATE_TIME_FIELD_NUMBER)
	public abstract LocalTime time();

	public interface Builder {
		Builder string(String string);
		Builder int64(long int64);
		Builder uuid(UUID uuid);
		Builder date(LocalDate date);
		Builder time(LocalTime time);

		ExampleModel build();
	}
}
