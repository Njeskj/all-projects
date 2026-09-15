package com.matching.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.65.1)",
    comments = "Source: matching.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class MatchingEngineGrpc {

  private MatchingEngineGrpc() {}

  public static final java.lang.String SERVICE_NAME = "matching.MatchingEngine";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.matching.grpc.SubmitOfferRequest,
      com.matching.grpc.SubmitOfferResponse> getSubmitOfferMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitOffer",
      requestType = com.matching.grpc.SubmitOfferRequest.class,
      responseType = com.matching.grpc.SubmitOfferResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.matching.grpc.SubmitOfferRequest,
      com.matching.grpc.SubmitOfferResponse> getSubmitOfferMethod() {
    io.grpc.MethodDescriptor<com.matching.grpc.SubmitOfferRequest, com.matching.grpc.SubmitOfferResponse> getSubmitOfferMethod;
    if ((getSubmitOfferMethod = MatchingEngineGrpc.getSubmitOfferMethod) == null) {
      synchronized (MatchingEngineGrpc.class) {
        if ((getSubmitOfferMethod = MatchingEngineGrpc.getSubmitOfferMethod) == null) {
          MatchingEngineGrpc.getSubmitOfferMethod = getSubmitOfferMethod =
              io.grpc.MethodDescriptor.<com.matching.grpc.SubmitOfferRequest, com.matching.grpc.SubmitOfferResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitOffer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.matching.grpc.SubmitOfferRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.matching.grpc.SubmitOfferResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MatchingEngineMethodDescriptorSupplier("SubmitOffer"))
              .build();
        }
      }
    }
    return getSubmitOfferMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MatchingEngineStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MatchingEngineStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MatchingEngineStub>() {
        @java.lang.Override
        public MatchingEngineStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MatchingEngineStub(channel, callOptions);
        }
      };
    return MatchingEngineStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MatchingEngineBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MatchingEngineBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MatchingEngineBlockingStub>() {
        @java.lang.Override
        public MatchingEngineBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MatchingEngineBlockingStub(channel, callOptions);
        }
      };
    return MatchingEngineBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MatchingEngineFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MatchingEngineFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MatchingEngineFutureStub>() {
        @java.lang.Override
        public MatchingEngineFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MatchingEngineFutureStub(channel, callOptions);
        }
      };
    return MatchingEngineFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void submitOffer(com.matching.grpc.SubmitOfferRequest request,
        io.grpc.stub.StreamObserver<com.matching.grpc.SubmitOfferResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitOfferMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service MatchingEngine.
   */
  public static abstract class MatchingEngineImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return MatchingEngineGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service MatchingEngine.
   */
  public static final class MatchingEngineStub
      extends io.grpc.stub.AbstractAsyncStub<MatchingEngineStub> {
    private MatchingEngineStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MatchingEngineStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MatchingEngineStub(channel, callOptions);
    }

    /**
     */
    public void submitOffer(com.matching.grpc.SubmitOfferRequest request,
        io.grpc.stub.StreamObserver<com.matching.grpc.SubmitOfferResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitOfferMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service MatchingEngine.
   */
  public static final class MatchingEngineBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<MatchingEngineBlockingStub> {
    private MatchingEngineBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MatchingEngineBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MatchingEngineBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.matching.grpc.SubmitOfferResponse submitOffer(com.matching.grpc.SubmitOfferRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitOfferMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service MatchingEngine.
   */
  public static final class MatchingEngineFutureStub
      extends io.grpc.stub.AbstractFutureStub<MatchingEngineFutureStub> {
    private MatchingEngineFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MatchingEngineFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MatchingEngineFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.matching.grpc.SubmitOfferResponse> submitOffer(
        com.matching.grpc.SubmitOfferRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitOfferMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SUBMIT_OFFER = 0;

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
        case METHODID_SUBMIT_OFFER:
          serviceImpl.submitOffer((com.matching.grpc.SubmitOfferRequest) request,
              (io.grpc.stub.StreamObserver<com.matching.grpc.SubmitOfferResponse>) responseObserver);
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
          getSubmitOfferMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.matching.grpc.SubmitOfferRequest,
              com.matching.grpc.SubmitOfferResponse>(
                service, METHODID_SUBMIT_OFFER)))
        .build();
  }

  private static abstract class MatchingEngineBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MatchingEngineBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.matching.grpc.Matching.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MatchingEngine");
    }
  }

  private static final class MatchingEngineFileDescriptorSupplier
      extends MatchingEngineBaseDescriptorSupplier {
    MatchingEngineFileDescriptorSupplier() {}
  }

  private static final class MatchingEngineMethodDescriptorSupplier
      extends MatchingEngineBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    MatchingEngineMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (MatchingEngineGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MatchingEngineFileDescriptorSupplier())
              .addMethod(getSubmitOfferMethod())
              .build();
        }
      }
    }
    return result;
  }
}
