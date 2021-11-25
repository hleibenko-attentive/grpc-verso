package io.github.heldev.verso.grpc.processor;

import java.util.List;


public final class DefinitionCatalog {
	private final List<MessageDefinition> messageDefinitions;

	private DefinitionCatalog(List<MessageDefinition> messageDefinitions) {
		this.messageDefinitions = messageDefinitions;
	}

	public static DefinitionCatalog of(List<MessageDefinition> messageDefinitions) {
		return new DefinitionCatalog(messageDefinitions);
	}

	public List<MessageDefinition> getMessageDefinitions() {
		return messageDefinitions;
	}

}


final class MessageDefinition {
	private final String protoPackage;
	private final String protoFile;
	private final String javaPackage;
	private final String name;
	private final List<MessageField> fields;

	MessageDefinition(String protoPackage, String protoFile, String javaPackage, String name, List<MessageField> fields) {
		this.protoPackage = protoPackage;
		this.protoFile = protoFile;
		this.javaPackage = javaPackage;
		this.name = name;
		this.fields = fields;
	}

	public static MessageDefinitionBuilder builder() {
		return new MessageDefinitionBuilder();
	}

	public String getProtoPackage() {
		return this.protoPackage;
	}

	public String getProtoFile() {
		return this.protoFile;
	}

	public String getJavaPackage() {
		return this.javaPackage;
	}

	public String getName() {
		return this.name;
	}

	public List<MessageField> getFields() {
		return this.fields;
	}

	public static class MessageDefinitionBuilder {
		private String protoPackage;
		private String protoFile;
		private String javaPackage;
		private String name;
		private List<MessageField> fields;

		MessageDefinitionBuilder() {
		}

		public MessageDefinitionBuilder protoPackage( String protoPackage) {
			this.protoPackage = protoPackage;
			return this;
		}

		public MessageDefinitionBuilder protoFile( String protoFile) {
			this.protoFile = protoFile;
			return this;
		}

		public MessageDefinitionBuilder javaPackage( String javaPackage) {
			this.javaPackage = javaPackage;
			return this;
		}

		public MessageDefinitionBuilder name( String name) {
			this.name = name;
			return this;
		}

		public MessageDefinitionBuilder fields( List<MessageField> fields) {
			this.fields = fields;
			return this;
		}

		public MessageDefinition build() {
			return new MessageDefinition(protoPackage, protoFile, javaPackage, name, fields);
		}

	}
}

final class MessageField {
	private final String name;
	private final int id;
	private final boolean isOptional;
	private final String type;

	MessageField(String name, int id, boolean isOptional, String type) {
		this.name = name;
		this.id = id;
		this.isOptional = isOptional;
		this.type = type;
	}

	public static MessageFieldBuilder builder() {
		return new MessageFieldBuilder();
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public boolean isOptional() {
		return this.isOptional;
	}

	public String getType() {
		return this.type;
	}

	public static class MessageFieldBuilder {
		private String name;
		private int id;
		private boolean isOptional;
		private String type;

		MessageFieldBuilder() {
		}

		public MessageFieldBuilder name(String name) {
			this.name = name;
			return this;
		}

		public MessageFieldBuilder id(int id) {
			this.id = id;
			return this;
		}

		public MessageFieldBuilder isOptional(boolean isOptional) {
			this.isOptional = isOptional;
			return this;
		}

		public MessageFieldBuilder type(String type) {
			this.type = type;
			return this;
		}

		public MessageField build() {
			return new MessageField(name, id, isOptional, type);
		}
	}
}
