package com.mtl.aio.coder;

import java.nio.charset.Charset;

/**
 * 说明:字符解码器
 *
 * @作者 莫天龙
 * @时间 2019/12/16 14:51
 */
public class ByteToStringDecoder implements MessageDecoder<byte[],String>  {
    private Charset charset;

    public ByteToStringDecoder(Charset charset) {
        this.charset = charset;
    }

    public ByteToStringDecoder() {
        charset=Charset.forName("UTF-8");
    }

    @Override
    public String decode(byte[] message) {
        return new String(message,charset);
    }
}
