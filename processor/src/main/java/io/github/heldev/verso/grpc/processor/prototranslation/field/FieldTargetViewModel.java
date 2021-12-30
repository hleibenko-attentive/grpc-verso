
package io.github.heldev.verso.grpc.processor.prototranslation.field;

import org.immutables.value.Value;

@Value.Immutable
public interface FieldTargetViewModel {
	static Builder builder() {
		return ImmutableFieldTargetViewModel.builder();
	}

	String builderMethodName();
	boolean builderAcceptsValue();

	interface Builder {
		Builder builderMethodName(String builderMethodName);
		Builder builderAcceptsValue(boolean builderAcceptsOptional);

		FieldTargetViewModel build();
	}
}
