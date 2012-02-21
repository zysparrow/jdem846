package us.wthr.jdem846.geom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TriangleStrip
{
	private List<Vertex> verteces = new LinkedList<Vertex>();
	
	
	public TriangleStrip()
	{
		
	}
	
	public void reset()
	{
		verteces.clear();
	}
	
	public int getVertexCount()
	{
		return verteces.size();
	}
	
	
	public int getTriangleCount()
	{
		int count = getVertexCount();
		if (count > 2) {
			return count - 2;
		} else {
			return 0;
		}
	}
	
	public void addVertex(Vertex vertex)
	{
		verteces.add(vertex);
	}
	
	public Triangle getTriangle(int index)
	{
		if (index < 0 || index >=  getTriangleCount()) {
			return null;
		}
		
		Vertex p0 = verteces.get(index);
		Vertex p1 = verteces.get(index + 1);
		Vertex p2 = verteces.get(index + 2);
		
		return new Triangle(p0, p1, p2);
	}
	
}