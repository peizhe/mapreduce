package org.zhaolei;  
import java.io.IOException;  
import java.net.URI;  
import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.fs.FileSystem;  
import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.io.IOUtils;  
import org.apache.hadoop.io.IntWritable;  
import org.apache.hadoop.io.SequenceFile;  
import org.apache.hadoop.io.Text;  
/** 
 * @author Eric.sunah 2014��12��10��
  * 
 */  
public class SequenceFileDemo {  
   
 private static final String OPERA_FILE = "./output.seq";  
 /** 
  * �������Ͻ�ȡ��һ���ı� 
  */  
 private static String[]    testArray = { "<plugin>                                                                     ",  
         "  <groupId>org.apache.avro</groupId>                                         ",  
         "  <artifactId>avro-maven-plugin</artifactId>                                 ",  
         "  <version>1.7.7</version>                                                   ",  
         "  <executions>                                                               ",  
         "    <execution>                                                              ",  
         "      <phase>generate-sources</phase>                                        ",  
         "      <goals>                                                                ",  
         "        <goal>schema</goal>                                                  ",  
         "      </goals>                                                               ",  
         "      <configuration>                                                        ",  
         "        <sourceDirectory>${project.basedir}/src/main/avro/</sourceDirectory> ",  
         "        <outputDirectory>${project.basedir}/src/main/java/</outputDirectory> ",  
         "      </configuration>                                                       ",  
         "    </execution>                                                             ",  
         "  </executions>                                                              ",  
         "</plugin>                                                                    ",  
         "<plugin>                                                                     ",  
         "  <groupId>org.apache.maven.plugins</groupId>                                ",  
         "  <artifactId>maven-compiler-plugin</artifactId>                             ",  
         "  <configuration>                                                            ",  
         "    <source>1.6</source>                                                     ",  
         "    <target>1.6</target>                                                     ",  
         "  </configuration>                                                           ", "</plugin>"};  
   
 public static void main(String[] args) throws IOException {  
  writeSequenceFile(OPERA_FILE);  
  readSequenceFile(OPERA_FILE);  
 }  
   
 private static void readSequenceFile(String inputFile) throws IOException {  
  Configuration config = new Configuration();  
  Path path = new Path(inputFile);  
  SequenceFile.Reader reader = null;  
  try {  
     
   FileSystem fs = FileSystem.get(URI.create(inputFile), config);  
   reader = new SequenceFile.Reader(fs, path, config);  
   IntWritable key = new IntWritable();  
   Text value = new Text();  
   long posion = reader.getPosition();  
   // reader.next()���طǿյĻ���ʾ���ڶ����������null��ʾ�Ѿ������ļ���β�ĵط�  
   while (reader.next(key, value)) {  
    //��ӡͬ�����λ����Ϣ  
    String syncMark = reader.syncSeen() ? "*" : "";  
    System.out.printf("[%s\t%s]\t%s\t%s\n", posion, syncMark, key, value);  
    posion = reader.getPosition();  
   }  
  } finally {  
   IOUtils.closeStream(reader);  
  }  
   
 }  
   
 /** 
  * дSequence File �ļ� 
  * 
  * @param outputFile 
  * @throws IOException 
  */  
 private static void writeSequenceFile(String outputFile) throws IOException {  
  Configuration config = new Configuration();  
  Path path = new Path(outputFile);  
  IntWritable key = new IntWritable();  
  Text value = new Text();  
  SequenceFile.Writer writer = null;  
  try {  
     
   FileSystem fs = FileSystem.get(URI.create(outputFile), config);  
   writer = SequenceFile.createWriter(fs, config, path, key.getClass(), value.getClass());  
   for (int i = 1; i < 2000; i++) {  
    key.set(2000 - i);  
    value.set(testArray[i % testArray.length]);  
    System.out.printf("[%s]\t%s\t%s\n", writer.getLength() + "", key, value);  
    // ͨ��Append��������д����  
    writer.append(key, value);  
   }  
  } finally {  
   IOUtils.closeStream(writer);  
  }  
   
 }  
}  
