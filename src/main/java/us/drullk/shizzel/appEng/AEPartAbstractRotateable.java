package us.drullk.shizzel.appEng;

import us.drullk.shizzel.appEng.enumList.AEParts;

public abstract class AEPartAbstractRotateable extends AEPartAbstract
{
    private static final String rotation = "partRotation";
    private byte renderRotation = 0;


    public AEPartAbstractRotateable(AEParts associatedPart) {
        super(associatedPart);
    }
}
