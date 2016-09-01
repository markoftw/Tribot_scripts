package scripts.Saltpetrev3;

import java.awt.*;
import java.util.*;

import org.tribot.api2007.types.RSTile;

public class Area {

	private Polygon areaPolygon;
	private LinkedList<Point> mPoints;

	public Polygon getAreaPolygon() {
		return this.areaPolygon;
	}

	public LinkedList<Point> getPoints() {
		return mPoints;
	}

	private final Polygon area;
	private final int plane;

	/**
	 * @param RSTiles
	 *            An Array containing of <b>RSTiles</b> forming a polygon shape.
	 * @param plane
	 *            The plane of the <b>Area</b>.
	 */
	public Area(RSTile[] RSTiles, int plane) {
		this.area = RSTileArrayToPolygon(RSTiles);
		this.plane = plane;
	}

	/**
	 * @param RSTiles
	 *            An Array containing of <b>RSTiles</b> forming a polygon shape.
	 */
	public Area(RSTile[] RSTiles) {
		this(RSTiles, 0);
	}

	/**
	 * @param sw
	 *            The <i>South West</i> <b>RSTile</b> of the <b>Area</b>
	 * @param ne
	 *            The <i>North East</i> <b>RSTile</b> of the <b>Area</b>
	 * @param plane
	 *            The plane of the <b>Area</b>.
	 */
	public Area(RSTile sw, RSTile ne, int plane) {
		this(new RSTile[] { sw, new RSTile(ne.getX() + 1, sw.getY()),
				new RSTile(ne.getX() + 1, ne.getY() + 1),
				new RSTile(sw.getX(), ne.getY() + 1) }, plane);
	}

	/**
	 * @param sw
	 *            The <i>South West</i> <b>RSTile</b> of the <b>Area</b>
	 * @param ne
	 *            The <i>North East</i> <b>RSTile</b> of the <b>Area</b>
	 */
	public Area(RSTile sw, RSTile ne) {
		this(sw, ne, 0);
	}

	/**
	 * @param swX
	 *            The X axle of the <i>South West</i> <b>RSTile</b> of the
	 *            <b>Area</b>
	 * @param swY
	 *            The Y axle of the <i>South West</i> <b>RSTile</b> of the
	 *            <b>Area</b>
	 * @param neX
	 *            The X axle of the <i>North East</i> <b>RSTile</b> of the
	 *            <b>Area</b>
	 * @param neY
	 *            The Y axle of the <i>North East</i> <b>RSTile</b> of the
	 *            <b>Area</b>
	 */
	public Area(int swX, int swY, int neX, int neY) {
		this(new RSTile(swX, swY), new RSTile(neX, neY), 0);
	}

	/**
	 * @param x
	 *            The x location of the <b>RSTile</b> that will be checked.
	 * @param y
	 *            The y location of the <b>RSTile</b> that will be checked.
	 * @return True if the <b>Area</b> contains the given <b>RSTile</b>.
	 */
	public boolean contains(int x, int y) {
		return this.contains(new RSTile(x, y));
	}

	/**
	 * @param plane
	 *            The plane to check.
	 * @param RSTiles
	 *            The <b>RSTile(s)</b> that will be checked.
	 * @return True if the <b>Area</b> contains the given <b>RSTile(s)</b>.
	 */
	public boolean contains(int plane, RSTile... RSTiles) {
		return this.plane == plane && this.contains(RSTiles);
	}

	/**
	 * @param RSTiles
	 *            The <b>RSTile(s)</b> that will be checked.
	 * @return True if the <b>Area</b> contains the given <b>RSTile(s)</b>.
	 */
	public boolean contains(RSTile... RSTiles) {
		RSTile[] areaRSTiles = this.getRSTileArray();
		for (RSTile check : RSTiles) {
			for (RSTile space : areaRSTiles) {
				if (check.equals(space)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return The central <b>RSTile</b> of the <b>Area</b>.
	 */
	public RSTile getCentralRSTile() {
		if (area.npoints < 1) {
			return null;
		}
		int totalX = 0, totalY = 0;
		for (int i = 0; i < area.npoints; i++) {
			totalX += area.xpoints[i];
			totalY += area.ypoints[i];
		}
		return new RSTile(Math.round(totalX / area.npoints), Math.round(totalY
				/ area.npoints));
	}

	/**
	 * @param base
	 *            The base RSTile to measure the closest RSTile off of.
	 * @return The nearest <b>RSTile</b> in the <b>Area</b> to the given
	 *         <b>RSTile</b>.
	 */
	public RSTile getNearestRSTile(RSTile base) {
		RSTile[] RSTiles = this.getRSTileArray();
		RSTile cur = null;
		double dist = -1;
		for (RSTile RSTile : RSTiles) {
			double distTmp = distanceBetween(RSTile, base);
			if (cur == null) {
				dist = distTmp;
				cur = RSTile;
			} else if (distTmp < dist) {
				cur = RSTile;
				dist = distTmp;
			}
		}
		return cur;
	}

	/**
	 * @return The <b>RSTiles</b> the <b>Area</b> contains.
	 */
	public RSTile[] getRSTileArray() {
		ArrayList<RSTile> list = new ArrayList<RSTile>();
		for (int x = this.getX(); x <= (this.getX() + this.getWidth()); x++) {
			for (int y = this.getY(); y <= (this.getY() + this.getHeight()); y++) {
				if (this.area.contains(x, y)) {
					list.add(new RSTile(x, y));
				}
			}
		}
		RSTile[] RSTiles = new RSTile[list.size()];
		for (int i = 0; i < list.size(); i++) {
			RSTiles[i] = list.get(i);
		}
		return RSTiles;
	}

	/**
	 * @return The <b>RSTiles</b> the <b>Area</b> contains.
	 */
	public RSTile[][] getRSTiles() {
		RSTile[][] RSTiles = new RSTile[this.getWidth()][this.getHeight()];
		for (int i = 0; i < this.getWidth(); ++i) {
			for (int j = 0; j < this.getHeight(); ++j) {
				if (this.area.contains(this.getX() + i, this.getY() + j)) {
					RSTiles[i][j] = new RSTile(this.getX() + i, this.getY() + j);
				}
			}
		}
		return RSTiles;
	}

	/**
	 * @return The distance between the the <b>RSTile</b> that's most
	 *         <i>East</i> and the <b>RSTile</b> that's most <i>West</i>.
	 */
	public int getWidth() {
		return this.area.getBounds().width;
	}

	/**
	 * @return The distance between the the <b>RSTile</b> that's most
	 *         <i>South</i> and the <b>RSTile</b> that's most <i>North</i>.
	 */
	public int getHeight() {
		return this.area.getBounds().height;
	}

	/**
	 * @return The X axle of the <b>RSTile</b> that's most <i>West</i>.
	 */
	public int getX() {
		return this.area.getBounds().x;
	}

	/**
	 * @return The Y axle of the <b>RSTile</b> that's most <i>South</i>.
	 */
	public int getY() {
		return this.area.getBounds().y;
	}

	/**
	 * @return The plane of the <b>Area</b>.
	 */
	public int getPlane() {
		return plane;
	}

	/**
	 * @return The bounding box of the <b>Area</b>.
	 */
	public Rectangle getBounds() {
		return new Rectangle(this.area.getBounds().x + 1,
				this.area.getBounds().y + 1, this.getWidth(), this.getHeight());
	}

	/**
	 * Converts an shape made of <b>RSTile</b> to a polygon.
	 * 
	 * @param RSTiles
	 *            The <b>RSTile</b> of the Polygon.
	 * @return The Polygon of the <b>RSTile</b>.
	 */
	private Polygon RSTileArrayToPolygon(RSTile[] RSTiles) {
		Polygon poly = new Polygon();
		for (RSTile t : RSTiles) {
			poly.addPoint(t.getX(), t.getY());
		}
		return poly;
	}

	/**
	 * @param curr
	 *            first <b>RSTile</b>
	 * @param dest
	 *            second <b>RSTile</b>
	 * @return the distance between the first and the second RSTile
	 */
	private double distanceBetween(RSTile curr, RSTile dest) {
		return Math.sqrt((curr.getX() - dest.getX())
				* (curr.getX() - dest.getX()) + (curr.getY() - dest.getY())
				* (curr.getY() - dest.getY()));
	}

}