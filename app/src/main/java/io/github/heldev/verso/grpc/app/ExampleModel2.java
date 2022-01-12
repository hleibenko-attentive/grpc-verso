package io.github.heldev.verso.grpc.app;

import io.github.heldev.verso.grpc.interfaces.VersoField;
import io.github.heldev.verso.grpc.interfaces.VersoGenerated;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import org.immutables.value.Value;

import java.util.UUID;

import static io.github.heldev.verso.grpc.app.Customizations.HARDCODED_UUID;
import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_INT64_FIELD_NUMBER;
import static io.github.heldev.verso.grpc.app.ExampleMessage.EXAMPLE_UUID_FIELD_NUMBER;

@VersoMessage("io.github.heldev.grpcverso.ExampleMessage")
@Value.Immutable
public interface ExampleModel2 {

	static Builder builder() {
		return ImmutableExampleModel2.builder();
	}

	@VersoField(EXAMPLE_INT64_FIELD_NUMBER)
	long int64();

	@VersoField(EXAMPLE_UUID_FIELD_NUMBER)
	UUID uuid();

	@VersoGenerated(HARDCODED_UUID)
	UUID generatedUuid();

	interface Builder {
		Builder int64(long int64);
		Builder uuid(UUID uuid);
		Builder generatedUuid(UUID uuid);

		ExampleModel2 build();
	}
}
