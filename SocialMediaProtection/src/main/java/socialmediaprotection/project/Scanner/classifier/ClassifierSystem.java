package socialmediaprotection.project.Scanner.classifier;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class ClassifierSystem {

    public void run () throws MonkeyLearnException {

        MonkeyLearn ml = new MonkeyLearn("7cf38d6b33e9655d6231ffe4fca502e60210aa79");
        String modelId = "cl_KFXhoTdt";
        String[] data = {"pahahahahhaahah omg thats funny shit", "what the hell"};
        MonkeyLearnResponse res = ml.classifiers.classify(modelId, data, false);
        System.out.println(res.arrayResult);
    }
}
