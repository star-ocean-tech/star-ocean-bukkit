package xianxian.mc.starocean.robot;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.eastrobot.ask.sdk.AskRequest;
import com.eastrobot.ask.sdk.AskResponse;
import com.eastrobot.ask.sdk.AskService;
import com.eastrobot.ask.sdk.CloudServiceFactory;
import com.eastrobot.ask.utils.Constant;

public class XiaoIRobot {
    private final static String appKey = "open1_sGF8nUmfgedY";
    private final static String appSecret = "tkuWF1Kic0sR01DhITp7";
    /**
     * 返回类型:Constant.PRIMARY_TYPE/Constant.SENIOR_TYPE
     */
    private final static String type = Constant.PRIMARY_TYPE;
    /**
     * 平台:请见Constant类后缀为_PLATFORM的常量
     */
    private final static String platform = Constant.CUSTOM_PLATFORM;

    private static AskService askService;

    private static Executor askExecutor = Executors.newFixedThreadPool(4);

    static {
        askService = CloudServiceFactory.getInstance().createAskService();
        askService.init(null);
    }

    public static void ask(String userId, String question, OnAskCallback callback) {
        // 智能问答
        AskRequest askRequest = new AskRequest(appKey, appSecret, question, type, userId, platform);
        Callable<AskResponse> askCallable = new Callable<AskResponse>() {
            @Override
            public AskResponse call() throws Exception {
                long time = System.currentTimeMillis();
                AskResponse response = askService.ask(askRequest);
                if ((System.currentTimeMillis() - time) < 10000)
                    callback.onCallback(response);
                else {
                    System.err.println("Ask Time out");
                    callback.onCallback(null);
                }
                return response;
            }
        };

        FutureTask<AskResponse> responseTask = new FutureTask<AskResponse>(askCallable);
//		Callable<Object> waitCallable = new Callable<Object>() {
//			@Override
//			public Object call() {
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if (!responseTask.isDone()) {
//					responseTask.cancel(true);
//					callback.onCallback(null);
//					
//				}
//				return null;
//			}
//		};
        // FutureTask<Object> waitTask = new FutureTask<Object>(waitCallable);
        askExecutor.execute(responseTask);
        // askExecutor.execute(waitTask);
    }

    public interface OnAskCallback {
        void onCallback(AskResponse response);
    }
}
