package com.github.aesteve.vertx.groovy

import groovy.lang.GString;import groovy.transform.TypeChecked
import io.vertx.groovy.core.buffer.Buffer;

@TypeChecked
class BufferExtension {

	static Buffer leftShift(Buffer self, Buffer other) {
		self.appendBuffer other
	}

	static Buffer leftShift(Buffer self, String other) {
		self.appendString other
	}

	static Buffer leftShift(Buffer self, GString other) {
		self.appendString other.toString()
	}

	static Buffer plus(Buffer self, Buffer other) {
		self.appendBuffer other
	}

	static Buffer plus(Buffer self, String other) {
		self.appendString other
	}

	static Buffer plus(Buffer self, GString other) {
		self.appendString other.toString()
	}

	static int compareTo(Buffer self, Object other) {
		if (!(other instanceof Buffer)) {
			return 1
		}
		Buffer otherBuff = (Buffer)other
		self.length().compareTo otherBuff.length()
	}
}
