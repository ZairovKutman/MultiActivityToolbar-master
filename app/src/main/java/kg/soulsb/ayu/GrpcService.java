/*
package com.mateoj.ayu;

import android.os.AsyncTask;

import com.mateoj.ayu.grpctest.nano.Agent;
import com.mateoj.ayu.grpctest.nano.AyuTesGrpc;
import com.mateoj.ayu.grpctest.nano.Point;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import static android.R.attr.name;

*/
/**
 * GrpcService для соединения и общения с сервером gRpc
 *
 * Created by Sultanbek Baibagyshev on 1/11/17.
 *//*


public class GrpcService {

    private ManagedChannel mChannel;
    private AyuTesGrpc.AyuTesBlockingStub blockingStub;
    private static final Logger logger = Logger.getLogger(GrpcService.class.getName());
    private Iterator<Point> response;
    */
/**
     * Конструктор для инициализации и соединения с сервером.
     * @param mHost String, имя или IP хоста
     * @param mPort Int, Порт хоста.
     *//*

    public GrpcService(String mHost, int mPort) {
        this(ManagedChannelBuilder.forAddress(mHost,mPort)
                .usePlaintext(true));
    }


    */
/**
     * Конструктор, для инициализации и соединения с сервером. Принимает ManagedChannelBuilder
     * @param channelBuilder ManagedChannelBuilder для соединения с сервером.
     *//*

    public GrpcService(ManagedChannelBuilder channelBuilder) {
        mChannel = channelBuilder.build();
        this.blockingStub = AyuTesGrpc.newBlockingStub(mChannel);
    }


    */
/**
     * Закрывает соединение (пока не используется, может пригодиться.)
     * @throws InterruptedException
     *//*

    public void shutdown() throws InterruptedException {
        mChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    */
/**
     * Получает список клиентов указанного агента.
     * @param name Имя агента
     * @return Возвращает Iterator в котором хранится список клиентов.
     *//*

    public Iterator<Point> getClients(String name) {
        logger.info("Will try to find points of  " + name + " ...");

        GrpcTask grpcTask = new GrpcTask(mChannel, name);

        Agent request = new Agent();
        request.name = name;

        try {
            grpcTask.execute();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return new Iterator<Point>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Point next() {
                    return null;
                }

                @Override
                public void remove() {

                }
            };
        }
        if (response.hasNext()) {
            for (Point point; response.hasNext(); ) {
                point = response.next();
                System.out.println(point.description);
            }
        }
        else
        {
            System.out.println("EMPTY List");
        }

        return response;
    }






}*/
