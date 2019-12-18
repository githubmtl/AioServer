package com.mtl.aio.coder;

import java.nio.charset.Charset;

/**
 * 说明:String编码器
 *
 * @作者 莫天龙
 * @时间 2019/12/17 10:58
 */
public class StringToByteEncoder implements MessageEncoder<String,byte[]>{
    private Charset charset;

    public StringToByteEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public byte[] encode(String message) {
        return message.getBytes(charset);
    }
}
