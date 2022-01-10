package io.github.heldev.verso.grpc.processor.common.type;

public enum ProtoTypeBasic implements ProtoType{
	BOOLEAN,
	INT,
	LONG,
	FLOAT,
	DOUBLE,
	BYTES,
	STRING;

	@Override
	public boolean isBasic() {
		return true;
	}
}
