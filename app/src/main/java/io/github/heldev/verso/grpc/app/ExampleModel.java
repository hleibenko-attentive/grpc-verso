package io.github.heldev.verso.grpc.app;

import io.github.heldev.verso.grpc.interfaces.VersoClass;
import io.github.heldev.verso.grpc.interfaces.VersoProperty;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@VersoClass("io.github.heldev.grpcverso.ExampleMessage")
public final class ExampleModel {
	public static void main(String[] args) {
		System.out.println("hi p");
	}

	private final String string;
	private final long int64;
	private final UUID uuid;

	public static Builder builder() {
		return new Builder();
	}

	public ExampleModel(String string, long int64, UUID uuid) {
		this.string = string;
		this.int64 = int64;
		this.uuid = uuid;
	}

	@VersoProperty(id = 1, nameCheck = "example_ field")
	public String getString() {
		return string;
	}

	@VersoProperty(id = 2, nameCheck = "example_int64")
	public long getInt64() {
		return int64;
	}

	@VersoProperty(id = 3, nameCheck = "example_uuid")
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		return "ExampleModel{" +
				"string='" + string + '\'' +
				", int64=" + int64 +
				", uuid=" + uuid +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ExampleModel that = (ExampleModel) o;
		return int64 == that.int64 && Objects.equals(string, that.string) && Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(string, int64, uuid);
	}

	public static class Builder {
		private String string;
		private Long int64;
		private UUID uuid;

		public Builder string(String string) {
			this.string = string;
			return this;
		}

		public Builder int64(long int64) {
			this.int64 = int64;
			return this;
		}

		public Builder uuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public ExampleModel build() {
			return new ExampleModel(
					requireNonNull(string, "missing string"),
					requireNonNull(int64, "missing int64"),
					requireNonNull(uuid, "missing uuid"));
		}
	}
}
