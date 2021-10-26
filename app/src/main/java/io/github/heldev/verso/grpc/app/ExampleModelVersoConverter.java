package io.github.heldev.verso.grpc.app;


public class ExampleModelVersoConverter {

	public ExampleModel fromProto(ExampleMessage proto) {
		return fromProtoPartial(proto).build();
	}

	public ExampleModel.Builder fromProtoPartial(ExampleMessage proto) {
		return ExampleModel.builder()
				.string(proto.getExampleString())
				.int64(proto.getExampleInt64())
				.uuid(CustomConverters.stringToUuid(proto.getExampleUuid()));
	}

}
