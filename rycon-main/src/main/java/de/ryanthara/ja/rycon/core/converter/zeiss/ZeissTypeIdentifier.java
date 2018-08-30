package de.ryanthara.ja.rycon.core.converter.zeiss;

/**
 * This enumeration holds the type identifications of the Zeiss REC files and it's formats (R4, R5, REC500 and M5).
 *
 * @author sebastian
 * @version 1
 * @since 12
 */
public enum ZeissTypeIdentifier {

    /*
    dialect independent
     */
    A("A "),     // addition constant
    ah("ah"),     // GPS antenna height
    B("B "),     // bow length
    C("C "),     // temperature in °C
    CD("CD"),     // control point distance
    CH("CH"),     // control point horizontal angle
    CV("CV"),     // control point vertical angle
    c("c "),     // target line improvement/enhancement
    c_("c_"),     // target line error
    D("D "),     // slope distance
    DI("DI"),     // prism constant
    Dh("Dh"),     // difference angle horizontal for sensor
    Dv("Dv"),     // difference angle vertical for sensor
    dL("dL"),     // stand point difference for levelling
    db("db"),     // angle closure error
    dF("dF"),     // difference current to target area
    dm("dm"),     // improvement/enhancement of the scale by adjustment
    dl("dl"),     // longitudinal deviation
    dq("dq"),     // lateral deviation
    //do  ("do"),   // improvement/enhancement of the orientation
    dr("dr"),     // radial improvement/enhancement
    ds("ds"),     // canal stick tolerance
    dx("dx"),     // x gape, coordinate difference
    dy("dy"),     // y gape, coordinate difference
    dz("dz"),     // z gape, coordinate difference
    dX("dX"),     // x gape at a helmert core.core.transformation
    dY("dY"),     // y gape at a helmert core.core.transformation
    dZ("dZ"),     // height difference
    E("E "),     // horizontal distance
    Ea("Ea"),     // maximum target distance at levelling
    ep("ep"),     // rotation of the helmert core.core.transformation
    F("F "),     // temperature in Fahrenheit
    Fl("Fl"),     // area in square metres
    HD("HD"),     // horizontal distance
    HV("HV"),     // horizontal rotation
    Hz("Hz"),     // horizontal angle
    h("h "),     // height difference
    hz("hz"),     // nominal value for horizontal angle
    i("i "),     // height index improvement/enhancement
    ih("ih"),       // instrument height
    KA("KA"),     // date
    KB("KB"),     // time
    KD("KD"),     // point identification at levelling
    KR("KR"),     // information with code and point number
    L("L "),     // levelling: bar meter reading
    Li("Li"),     // minimal target height at levelling
    Lr("Lr"),     // levelling: bar meter reading backsight
    Lv("Lv"),     // levelling: bar meter reading foresight
    Lx("Lx"),     // levelling: bar offset
    Lz("Lz"),     // levelling: bar meter reading intermediate sight
    m("m "),     // scale
    m0("m0"),     // average error/point defect
    mm("mm"),     // average scale error
    mx("mx"),     // average error/point defect x direction
    my("my"),     // average error/point defect y direction
    mz("mz"),     // average error/point defect z direction
    np("np"),     // number of surface corner points
    O("O "),     // transverse at indirect height determination
    o("o "),     // parameter of the helmert core.core.transformation
    P("P "),     // air pressure 0=hPa or Torr, 1=InMerc
    PI("PI"),     // point identification
    Ps("Ps"),     // point identification for station coordinates
    Pz("Pz"),     // point identification for target coordinates
    pm("pm"),     // correction in ppm
    pr("pr"),     // weight of an orientation at an adjustment
    ps("ps"),     // weight of a distance at an adjustment
    R("R "),     // radius at aligning
    Ri("Ri"),     // orientation angle at steak out for approach or levelling: minimum sight height
    SD("SD"),     // slope distance
    Sr("Sr"),     // sum of backsight distances
    Sv("Sv"),     // sum of foresight distances
    T("T "),     // text information
    TG("TG"),     // text in basic condition
    TI("TI"),     // text information line
    TO("TO"),     // general text information at levelling
    TP("TP"),     // text project information
    TR("TR"),     // text information
    T_("T_"),     // temperature in °C or F
    Tl("Tl"),     // ex centre left
    Th("Th"),     // ex centre behind
    Tr("Tr"),     // ex centre right
    Ts("Ts"),     // ex centre slope distance
    Tv("Tv"),     // ex centre front
    To("To"),     // reflector height at canal measuring stick (top)
    Tu("Tu"),     // reflector height at canal measuring stick (bottom)
    th("th"),     // target height
    tr("tr"),     // target height backsight
    tv("tv"),     // target height foresight
    V1("V1"),     // vertical angle: zenith angle
    V2("V2"),     // vertical angle: vertical angle
    V3("V3"),     // vertical angle: height angle
    V4("V4"),     // vertical angle: inclination in percent
    Vo("Vo"),     // volume
    v1("v1"),     // desired value for v1 zenith angle
    vl("vl"),     // length gap after adjustment
    vq("vq"),     // transverse gap after adjustment
    va("va"),     // horizontal orientation gap after adjustment
    X("X "),     // x coordinate (northing)
    x("x "),     // x coordinate (local northing)
    X_("X_"),     // x coordinate (geocentric)
    Y("Y "),     // y coordinate (easting)
    y("y "),     // y coordinate (local easting)
    Y_("Y_"),     // y coordinate (geocentric)
    Z("Z "),     // z coordinate (height)
    ZE("ZE"),     // z coordinate (ellipsoid height)
    Z_("Z_"),     // z coordinate (geocentric)
//    ?   ("? "),     // wrong data

    /*
    dialect dependent
     */
    de("de"),     // coordinate difference (easting)
    dn("dn"),     // coordinate difference (northing)
    dE("dE"),     // coordinate difference (easting gap) at helmert core.core.transformation
    dN("dN"),     // coordinate difference (northing gap) at helmert core.core.transformation
    Db("Db"),     // levelling: total of backsight distances
    Df("Df"),     // levelling: total of foresight distance
    dR("dR"),     // levelling station difference
    E_("E_"),     // x coordinate (easting)
    e("e "),     // x coordinate (easting local)
    KN("KN"),     // point identification, beginning and end of a levelling line
    me("me"),     // middle coordinate error (easting)
    mn("mn"),     // middle coordinate error (northing)
    N_("N_"),     // northing coordinate
    n("n "),     // northing coordinate local
    Rb("Rb"),     // levelling: staff reading backsight
    Rf("Rf"),     // levelling: staff reading foresight
    Rz("Rz"),     // levelling: bar meter reading intermediate sight
    TN("TN"),     // levelling: text information
    ;

    private final String typeIdentifier;

    /**
     * Defines a {@link ZeissTypeIdentifier} by a type identifier string.
     *
     * @param typeIdentifier the type identifier
     */
    ZeissTypeIdentifier(String typeIdentifier) {
        this.typeIdentifier = typeIdentifier;
    }

    /**
     * Returns the type identifier string.
     *
     * @return the type identifier string
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return typeIdentifier;
    }

} // end of ZeissTypeIdentifier
