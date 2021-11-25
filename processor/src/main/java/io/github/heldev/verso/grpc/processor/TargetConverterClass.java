package io.github.heldev.verso.grpc.processor;

public class TargetConverterClass {

	private final String javaPackage;
	private final String name;
	private final TargetClass targetClass;
	private final MessageDefinition sourceMessage;


	public TargetConverterClass(String javaPackage, String name, TargetClass targetClass, MessageDefinition sourceMessage) {
		this.javaPackage = javaPackage;
		this.name = name;
		this.targetClass = targetClass;
		this.sourceMessage = sourceMessage;
	}
}
