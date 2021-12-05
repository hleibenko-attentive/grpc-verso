package io.github.heldev.verso.grpc.app;

import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.interfaces.VersoField;
import org.immutables.value.Value;

import java.util.UUID;

@VersoMessage("io.github.heldev.grpcverso.ExampleMessage")
@Value.Immutable
public abstract class ExampleModel {

	public static Builder builder() {
		return ImmutableExampleModel.builder();
	}

	@VersoField(ExampleMessage.EXAMPLE_STRING_FIELD_NUMBER)
	public abstract String string();

	@VersoField(ExampleMessage.EXAMPLE_INT64_FIELD_NUMBER)
	public abstract long int64();

	public abstract UUID uuid();

	public interface Builder {
		Builder string(String string);
		Builder int64(long int64);

		Builder uuid(UUID uuid);

		ExampleModel build();
	}
}
