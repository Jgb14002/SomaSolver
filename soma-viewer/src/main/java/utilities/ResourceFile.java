package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;

public class ResourceFile
{
	private final String fileName;
	private String path;

	public ResourceFile(String path, String fileName)
	{
		this.path = path;
		this.fileName = fileName;
	}

	public ResourceFile(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFullPath()
	{
		return (path == null) ? fileName : String.format("%s%s%s", path, File.separator, fileName);
	}

	public InputStream getInputStream()
	{
		return ResourceFile.class.getClassLoader().getResourceAsStream(getFullPath());
	}

	public BufferedReader getReader()
	{
		InputStreamReader isr = new InputStreamReader(getInputStream());
		return new BufferedReader(isr);
	}

	public ByteBuffer getByteBuffer()
	{
		ByteBuffer buffer = null;
		try (InputStream stream = getInputStream())
		{
			byte[] bytes = stream.readAllBytes();
			buffer = BufferUtils.createByteBuffer(bytes.length);
			buffer.put(bytes);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return (buffer == null) ? null : buffer.flip();
	}

}
