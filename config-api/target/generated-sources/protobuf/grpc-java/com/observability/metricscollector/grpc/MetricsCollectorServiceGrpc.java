package com.observability.metricscollector.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.66.0)",
    comments = "Source: metrics.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class MetricsCollectorServiceGrpc {

  private MetricsCollectorServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "metrics.MetricsCollectorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.observability.metricscollector.grpc.IngestMetricRequest,
      com.observability.metricscollector.grpc.IngestMetricResponse> getIngestMetricMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "IngestMetric",
      requestType = com.observability.metricscollector.grpc.IngestMetricRequest.class,
      responseType = com.observability.metricscollector.grpc.IngestMetricResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.observability.metricscollector.grpc.IngestMetricRequest,
      com.observability.metricscollector.grpc.IngestMetricResponse> getIngestMetricMethod() {
    io.grpc.MethodDescriptor<com.observability.metricscollector.grpc.IngestMetricRequest, com.observability.metricscollector.grpc.IngestMetricResponse> getIngestMetricMethod;
    if ((getIngestMetricMethod = MetricsCollectorServiceGrpc.getIngestMetricMethod) == null) {
      synchronized (MetricsCollectorServiceGrpc.class) {
        if ((getIngestMetricMethod = MetricsCollectorServiceGrpc.getIngestMetricMethod) == null) {
          MetricsCollectorServiceGrpc.getIngestMetricMethod = getIngestMetricMethod =
              io.grpc.MethodDescriptor.<com.observability.metricscollector.grpc.IngestMetricRequest, com.observability.metricscollector.grpc.IngestMetricResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "IngestMetric"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.observability.metricscollector.grpc.IngestMetricRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.observability.metricscollector.grpc.IngestMetricResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MetricsCollectorServiceMethodDescriptorSupplier("IngestMetric"))
              .build();
        }
      }
    }
    return getIngestMetricMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MetricsCollectorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MetricsCollectorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MetricsCollectorServiceStub>() {
        @java.lang.Override
        public MetricsCollectorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MetricsCollectorServiceStub(channel, callOptions);
        }
      };
    return MetricsCollectorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MetricsCollectorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MetricsCollectorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MetricsCollectorServiceBlockingStub>() {
        @java.lang.Override
        public MetricsCollectorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MetricsCollectorServiceBlockingStub(channel, callOptions);
        }
      };
    return MetricsCollectorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MetricsCollectorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MetricsCollectorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MetricsCollectorServiceFutureStub>() {
        @java.lang.Override
        public MetricsCollectorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MetricsCollectorServiceFutureStub(channel, callOptions);
        }
      };
    return MetricsCollectorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void ingestMetric(com.observability.metricscollector.grpc.IngestMetricRequest request,
        io.grpc.stub.StreamObserver<com.observability.metricscollector.grpc.IngestMetricResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getIngestMetricMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service MetricsCollectorService.
   */
  public static abstract class MetricsCollectorServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return MetricsCollectorServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service MetricsCollectorService.
   */
  public static final class MetricsCollectorServiceStub
      extends io.grpc.stub.AbstractAsyncStub<MetricsCollectorServiceStub> {
    private MetricsCollectorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MetricsCollectorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MetricsCollectorServiceStub(channel, callOptions);
    }

    /**
     */
    public void ingestMetric(com.observability.metricscollector.grpc.IngestMetricRequest request,
        io.grpc.stub.StreamObserver<com.observability.metricscollector.grpc.IngestMetricResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getIngestMetricMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service MetricsCollectorService.
   */
  public static final class MetricsCollectorServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<MetricsCollectorServiceBlockingStub> {
    private MetricsCollectorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MetricsCollectorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MetricsCollectorServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.observability.metricscollector.grpc.IngestMetricResponse ingestMetric(com.observability.metricscollector.grpc.IngestMetricRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getIngestMetricMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service MetricsCollectorService.
   */
  public static final class MetricsCollectorServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<MetricsCollectorServiceFutureStub> {
    private MetricsCollectorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MetricsCollectorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MetricsCollectorServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.observability.metricscollector.grpc.IngestMetricResponse> ingestMetric(
        com.observability.metricscollector.grpc.IngestMetricRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getIngestMetricMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INGEST_METRIC = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INGEST_METRIC:
          serviceImpl.ingestMetric((com.observability.metricscollector.grpc.IngestMetricRequest) request,
              (io.grpc.stub.StreamObserver<com.observability.metricscollector.grpc.IngestMetricResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getIngestMetricMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.observability.metricscollector.grpc.IngestMetricRequest,
              com.observability.metricscollector.grpc.IngestMetricResponse>(
                service, METHODID_INGEST_METRIC)))
        .build();
  }

  private static abstract class MetricsCollectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MetricsCollectorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.observability.metricscollector.grpc.Metrics.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MetricsCollectorService");
    }
  }

  private static final class MetricsCollectorServiceFileDescriptorSupplier
      extends MetricsCollectorServiceBaseDescriptorSupplier {
    MetricsCollectorServiceFileDescriptorSupplier() {}
  }

  private static final class MetricsCollectorServiceMethodDescriptorSupplier
      extends MetricsCollectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    MetricsCollectorServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MetricsCollectorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MetricsCollectorServiceFileDescriptorSupplier())
              .addMethod(getIngestMetricMethod())
              .build();
        }
      }
    }
    return result;
  }
}
