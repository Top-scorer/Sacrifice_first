package com.fzb.utils;

import com.fzb.pojo.SentimentResult;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;

import java.util.Properties;

public class NlpPipeline {
    StanfordCoreNLP pipeline = null;
    public  void init()
    {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }
    public SentimentResult estimatingSentiment(String text)
    {
        // 初始化整体统计变量
        double[] sumProbs = new double[5]; // 累计5个情感类别的概率和
        int totalSentences = 0;

        int sentimentInt;
        String sentimentName;
        Annotation annotation = pipeline.process(text);
        for(CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class))
        {
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
            sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            // 获取情感概率分布（5维向量）
            SimpleMatrix sentimentProbs = RNNCoreAnnotations.getPredictions(tree);

            // 提取每个类别的分数
            double veryNegative = sentimentProbs.get(0);  // Very negative
            double negative = sentimentProbs.get(1);      // Negative
            double neutral = sentimentProbs.get(2);       // Neutral
            double positive = sentimentProbs.get(3);      // Positive
            double veryPositive = sentimentProbs.get(4);  // Very positive

            // 累加每个类别的概率
            for (int i = 0; i < 5; i++) {
                sumProbs[i] += sentimentProbs.get(i);
            }
            totalSentences ++;

            // 打印结果
            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
            System.out.println("Very Negative: " + veryNegative);
            System.out.println("Negative: " + negative);
            System.out.println("Neutral: " + neutral);
            System.out.println("Positive: " + positive);
            System.out.println("Very Positive: " + veryPositive);
        }
        // 计算整体概率分布（平均）
        double[] avgProbs = new double[5];
        for (int i = 0; i < 5; i++) {
            avgProbs[i] = sumProbs[i] / totalSentences;
        }

        // 确定整体情感类别（取概率最高的类别）
        short overallClass = 0;
        double maxProb = avgProbs[0];
        for (int i = 1; i < 5; i++) {
            if (avgProbs[i] > maxProb) {
                maxProb = avgProbs[i];
                overallClass = (short)i;
            }
        }

        System.out.println("整体情感类别：" + overallClass);
        System.out.println("整体情感概率：" + maxProb);

        SentimentResult sentimentResult = new SentimentResult();
        sentimentResult.setSentimentScore(overallClass);//整体情感类别
        sentimentResult.setSentimentProb(maxProb);//平均最高概率（整体情感概率）
        return sentimentResult;
    }
}