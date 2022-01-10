package io.github.heldev.verso.grpc.processor.common;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

import static io.github.heldev.verso.grpc.processor.common.NamingConventions.toSanitizedCapitalCamelCase;
import static io.github.heldev.verso.grpc.processor.common.Utils.optionalWhen;

@Value.Immutable
public abstract class MessageDefinitionContext {

	public static Builder builder() {
		return ImmutableMessageDefinitionContext.builder();
	}

	public abstract Optional<String> protoPackage();

	public abstract List<String> outerMessageNames();

	public abstract Optional<String> javaPackage();

	public Optional<String> buildOuterJavaClassName() {
		return optionalWhen(
				! isMultipleFiles(),
				() -> outerClassName().orElseGet(() -> toSanitizedCapitalCamelCase(filenameStem())));
	}

	protected abstract boolean isMultipleFiles();

	protected abstract Optional<String> outerClassName();

	private String filenameStem() {
		return protoFilepath()
				.replaceAll("^.*/", "")
				.replaceAll("\\.proto$", "");
	}

	protected abstract String protoFilepath();

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public interface Builder {
		Builder protoPackage(Optional<String> protoPackage);
		Builder javaPackage(Optional<String> javaPackage);
		Builder isMultipleFiles(boolean isMultipleFiles);
		Builder outerClassName(Optional<String> outerClassName);
		Builder protoFilepath(String protoFilepath);

		MessageDefinitionContext build();
	}
}
