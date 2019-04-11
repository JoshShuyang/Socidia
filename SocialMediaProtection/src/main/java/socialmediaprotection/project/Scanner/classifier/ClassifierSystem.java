package socialmediaprotection.project.Scanner.classifier;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;
import org.json.simple.JSONArray;

public class ClassifierSystem {

    private static final String PROFANITY = "cl_KFXhoTdt";
    private static final String BUSINESS = "cl_5vWJMjGc";

    private String[] data;

    public ClassifierSystem(String[] data) {
        this.data = data;
    }

    public JSONArray run (String modelId) throws MonkeyLearnException {
        MonkeyLearn ml = new MonkeyLearn("7cf38d6b33e9655d6231ffe4fca502e60210aa79");
        MonkeyLearnResponse res = ml.classifiers.classify(modelId, data, false);

        return res.arrayResult;
    }

    public JSONArray checkProfanity() throws MonkeyLearnException {
        return run(PROFANITY);
    }

    public JSONArray checkBusiness() throws MonkeyLearnException {
        return run(BUSINESS);
    }
}
