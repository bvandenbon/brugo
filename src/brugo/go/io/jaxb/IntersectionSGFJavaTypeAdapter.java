package brugo.go.io.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import brugo.go.bo.state.Intersection;
import brugo.go.io.sgf.SGFEncoder;

public final class IntersectionSGFJavaTypeAdapter extends XmlAdapter<String, Intersection>
{
  private static final SGFEncoder ENCODER = new SGFEncoder();

  @Override
  public Intersection unmarshal(String v) throws Exception
  {
    return ENCODER.fromCoordinateSGF(v);
  }

  @Override
  public String marshal(Intersection v) throws Exception
  {
    return ENCODER.toSGF(v);
  }
}
