package com.kidozh.npuhelper.bbsService;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public abstract class ResponseHandler {
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private static final int MSG_START = 2;
    private static final int MSG_FINISH = 3;
    private static final int MSG_PROGRESS = 4;
    private static final int MSG_START_DOWN = 5;

    private Handler handler;
    private Looper looper = null;

    public ResponseHandler() {
        this(null);
    }

    public ResponseHandler(Looper looper) {
        this.looper = (looper == null ? Looper.getMainLooper() : looper);
        handler = new ResponderHandler(this, this.looper);
    }

    public void onStart() {

    }

    public void onProgress(int progress, long totalBytes) {
    }

    public void onFinish() {
    }

    public void onStartDownload(String fileName) {

    }

    public abstract void onSuccess(byte[] response);

    public void onFailure(Throwable e) {

    }


    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SUCCESS:
                onSuccess((byte[]) msg.obj);
                break;
            case MSG_FAILURE:
                onFailure((Throwable) msg.obj);
                break;
            case MSG_START:
                onStart();
                break;
            case MSG_FINISH:
                onFinish();
                break;
            case MSG_PROGRESS:
                Object[] obj = (Object[]) msg.obj;
                if (obj != null && obj.length >= 2) {
                    onProgress((int) obj[0], (Long) obj[1]);
                }
                break;
            case MSG_START_DOWN:
                onStartDownload((String) msg.obj);
                break;
            default:
                Log.w(getClass().getName(), "unknown msg type: " + msg.what);
                break;
        }
    }


    final void sendStartDownloadMessage(String fileName) {
        handleMessage(obtainMessage(MSG_START_DOWN, fileName));
    }

    final void sendProgressMessage(int progress, long bytesTotal) {
        handler.sendMessage(obtainMessage(MSG_PROGRESS, new Object[]{progress, bytesTotal}));
    }

    final void sendSuccessMessage(byte[] responseBytes) {
        handler.sendMessage(obtainMessage(MSG_SUCCESS, responseBytes));
    }

    final void sendFailureMessage(Throwable throwable) {
        handler.sendMessage(obtainMessage(MSG_FAILURE, throwable));
    }

    final void sendStartMessage() {
        handler.sendMessage(obtainMessage(MSG_START, null));
    }

    final void sendFinishMessage() {
        handler.sendMessage(obtainMessage(MSG_FINISH, null));
    }

    private Message obtainMessage(int responseMessageId, Object responseMessageData) {
        return Message.obtain(handler, responseMessageId, responseMessageData);
    }

    private byte[] readFrom(InputStream inputStream, long length) throws IOException {
        if (inputStream == null) {
            return new byte[0];
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        return os.toByteArray();
    }

    void processResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        long contentLength = connection.getContentLength();
        if (responseCode >= 200 && responseCode < 303) {
            byte[] responseContent = readFrom(connection.getInputStream(), contentLength);
            sendSuccessMessage(responseContent);
        } else {
            sendFailureMessage(new Throwable("responseCode is " + responseCode));
        }
    }

    /**
     * Avoid leaks by using a non-anonymous handler class.
     */
    private static class ResponderHandler extends Handler {
        private final ResponseHandler mResponder;

        ResponderHandler(ResponseHandler mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }

}
