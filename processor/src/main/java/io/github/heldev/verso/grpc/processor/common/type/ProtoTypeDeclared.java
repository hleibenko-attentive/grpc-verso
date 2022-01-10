package io.github.heldev.verso.grpc.processor.common.type;

import org.immutables.value.Value;

@Value.Immutable
public interface ProtoTypeDeclared extends ProtoType {

	static ProtoTypeDeclared enumType(String qualifiedName) {
		return ImmutableProtoTypeDeclared.builder()
				.isEnum(true)
				.qualifiedName(qualifiedName)
				.build();
	}

	static ProtoTypeDeclared message(String qualifiedName) {
		return ImmutableProtoTypeDeclared.builder()
				.isEnum(false)
				.qualifiedName(qualifiedName)
				.build();
	}

	@Override
	default boolean isBasic() {
		return false;
	}

	boolean isEnum();
	String qualifiedName();
}
