package com.sigmoid.parser;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shuyang on 2017/12/16.
 */
public class TextParser {

    private static StanfordCoreNLP pipeline;

    static {
        pipeline = new StanfordCoreNLP("CoreNLP-chinese.properties");
    }

    private static List<String> parse(String text) {

        if(StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }


        // 用一些文本来初始化一个注释。文本是构造函数的参数。
        Annotation annotation = new Annotation(text);
        // 运行所有选定的代码在本文
        pipeline.annotate(annotation);

        // 从注释中获取CoreMap List，并取第0个值
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap sentence = sentences.get(0);

        // 从CoreMap中取出CoreLabel List，逐一打印出来
        List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

        return tokens.stream().map(t -> t.getString(CoreAnnotations.TextAnnotation.class).trim())
                .collect(Collectors.toList());

    }

    public static void main(String[] args) throws IOException {

        String path = args[0];

        String outFile = args[1];

        FileOutputStream fileOutputStream = new FileOutputStream(new File(outFile));

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
                String line = null;
                // 一次读入一行，直到读入null为文件结束
                while ((line = reader.readLine()) != null) {
                    List<String> words = parse(line);
                    if(CollectionUtils.isEmpty(words)) {
                        continue;
                    }
                    String s = StringUtils.join(words, " ") + "\n";
                    fileOutputStream.write(s.getBytes(Charset.defaultCharset()));
                }
            }
        } finally {
            fileOutputStream.close();
        }

    }

}
