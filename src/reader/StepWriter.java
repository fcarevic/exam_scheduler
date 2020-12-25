package reader;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import csp.Exam;
import csp.Triplet;

public class StepWriter {
		Writer writer =null;
		public void init(String filename) throws UnsupportedEncodingException, FileNotFoundException {
			this.writer = new BufferedWriter(new OutputStreamWriter(
				    new FileOutputStream(filename), "UTF-8"));
		}
		
		public void flush() throws IOException {
			writer.flush();
		}
		
		public void close() throws IOException {
			writer.close();
		}
		
		public void writeString(String message) throws IOException {
			writer.write(message);
		}
		
		public void writeSolution(Exam exam, List<Triplet> addedValues ) throws IOException {
			StringBuilder sb= new StringBuilder();
			sb.append(exam.getCode()+ "  =   ");
			for(Triplet t:addedValues) {
				sb.append(t.toString() + "  //  ");
				
			}
			sb.append('\n');
			writer.write(sb.toString() );
		}
	
}
