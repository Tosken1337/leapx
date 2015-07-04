/*
 * (C) Copyright 2015 Richard Greenlees

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Contains the definition of a Vector comprising 3 doubles and associated
 * transformations.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
public class Vector3d implements Serializable, Externalizable {

    private static final long serialVersionUID = 1L;   

    public double x;
    public double y;
    public double z;

    /**
     * Create a new {@link Vector3d} with all components set to zero.
     */
    public Vector3d() {
    }

    /**
     * Create a new {@link Vector3d} with the given component values.
     * 
     * @param x
     * @param y
     * @param z
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create a new {@link Vector3d} whose values will be copied from the given vector.
     * 
     * @param v
     *          provides the initial values for the new vector
     */
    public Vector3d(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Create a new {@link Vector3d} whose values will be copied from the given vector.
     * 
     * @param v
     *          provides the initial values for the new vector
     */
    public Vector3d(Vector3d v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Set the x, y and z attributes to match the supplied vector.
     * 
     * @param v
     *          the vector to set this vector's components from
     * @return this
     */
    public Vector3d set(Vector3d v) {
        x = v.x;
        y = v.y;
        z = v.z;
        return this;
    }

    /**
     * Set the x, y and z attributes to match the supplied vector.
     * 
     * @param v
     *          the vector to set this vector's components from
     * @return this
     */
    public Vector3d set(Vector3f v) {
        x = v.x;
        y = v.y;
        z = v.z;
        return this;
    }

    /**
     * Set the x, y and z attributes to the supplied float values.
     * 
     * @return this
     */
    public Vector3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Subtract the supplied vector from this one.
     * 
     * @return this
     */
    public Vector3d sub(Vector3d v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    /**
     * Subtract the supplied vector from this one and store the result in <code>dest</code>.
     * 
     * @param v
     *          the vector to subtract from <code>this</code>
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d sub(Vector3d v, Vector3d dest) {
        dest.x = x - v.x;
        dest.y = y - v.y;
        dest.z = z - v.z;
        return this;
    }

    /**
     * Subtract the supplied vector from this one.
     * 
     * @return this
     */
    public Vector3d sub(Vector3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    /**
     * Subtract the supplied vector from this one and store the result in <code>dest</code>.
     * 
     * @param v
     *          the vector to subtract from <code>this</code>
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d sub(Vector3f v, Vector3d dest) {
        dest.x = x - v.x;
        dest.y = y - v.y;
        dest.z = z - v.z;
        return this;
    }

    /**
     * Subtract <tt>(x, y, z)</tt> from this vector.
     * 
     * @return this
     */
    public Vector3d sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * Subtract <tt>(x, y, z)</tt> from this vector and store the result in <code>dest</code>.
     * 
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d sub(double x, double y, double z, Vector3d dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        return this;
    }

    /**
     * Subtract v2 from v1 and store the result in dest.
     */
    public static void sub(Vector3f v1, Vector3d v2, Vector3d dest) {
        dest.x = v1.x - v2.x;
        dest.y = v1.y - v2.y;
        dest.z = v1.z - v2.z;
    }

    /**
     * Add the supplied vector to this one.
     * 
     * @return this
     */
    public Vector3d add(Vector3d v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    /**
     * Add the supplied vector to this one and store the result in <code>dest</code>.
     * 
     * @param v
     *          the vector to add
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d add(Vector3d v, Vector3d dest) {
        dest.x = x + v.x;
        dest.y = y + v.y;
        dest.z = z + v.z;
        return this;
    }

    /**
     * Add the supplied vector to this one.
     * 
     * @return this
     */
    public Vector3d add(Vector3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    /**
     * Add the supplied vector to this one and store the result in <code>dest</code>.
     * 
     * @param v
     *          the vector to add
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d add(Vector3f v, Vector3d dest) {
        dest.x = x + v.x;
        dest.y = y + v.y;
        dest.z = z + v.z;
        return this;
    }

    /**
     * Add v2 to v1 and store the result in dest.
     */
    public static void add(Vector3f v1, Vector3d v2, Vector3d dest) {
        dest.x = v1.x + v2.x;
        dest.y = v1.y + v2.y;
        dest.z = v1.z + v2.z;
    }

    /**
     * Add the component-wise multiplication of <code>a * b</code> to this vector.
     * 
     * @param a
     *          the first multiplicand
     * @param b
     *          the second multiplicand
     * @return this
     */
    public Vector3d fma(Vector3d a, Vector3d b) {
        x += a.x * b.x;
        y += a.y * b.y;
        z += a.z * b.z;
        return this;
    }

    /**
     * Add the component-wise multiplication of <code>a * b</code> to this vector
     * and store the result in <code>dest</code>.
     * 
     * @param a
     *          the first multiplicand
     * @param b
     *          the second multiplicand
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d fma(Vector3d a, Vector3d b, Vector3d dest) {
        dest.x = x + a.x * b.x;
        dest.y = y + a.y * b.y;
        dest.z = z + a.z * b.z;
        return this;
    }

    /**
     * Multiply this Vector3d component-wise by another Vector3d.
     * 
     * @return this
     */
    public Vector3d mul(Vector3d v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }

    /**
     * Multiply this Vector3d component-wise by another Vector3f.
     * 
     * @return this
     */
    public Vector3d mul(Vector3f v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }

    /**
     * Multiply this by <code>v</code> component-wise and store the result into <code>dest</code>.
     * 
     * @return this
     */
    public Vector3d mul(Vector3d v, Vector3d dest) {
        dest.x = x * v.x;
        dest.y = y * v.y;
        dest.z = z * v.z;
        return this;
    }

    /**
     * Multiply this Vector3d by the given matrix <code>mat</code> and store the result in <code>dest</code>.
     * <p>
     * This method uses <tt>w=1.0</tt> as the fourth vector component.
     * 
     * @param mat
     *          the matrix to multiply this vector by
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d mul(Matrix4d mat, Vector3d dest) {
        if (this != dest) {
            dest.x = mat.m00 * x + mat.m10 * y + mat.m20 * z;
            dest.y = mat.m01 * x + mat.m11 * y + mat.m21 * z;
            dest.z = mat.m02 * x + mat.m12 * y + mat.m22 * z;
        } else {
            dest.set(mat.m00 * x + mat.m10 * y + mat.m20 * z,
                     mat.m01 * x + mat.m11 * y + mat.m21 * z,
                     mat.m02 * x + mat.m12 * y + mat.m22 * z);
        }
        return this;
    }

    /**
     * Multiply this Vector3d by the given matrix <code>mat</code>.
     * <p>
     * This method uses <tt>w=1.0</tt> as the fourth vector component.
     * 
     * @return this
     */
    public Vector3d mul(Matrix4d mat) {
        return mul(mat, this);
    }

    /**
     * Multiply this Vector3d by the given matrix <code>mat</code> and store the result in <code>dest</code>.
     * <p>
     * This method uses <tt>w=1.0</tt> as the fourth vector component.
     * 
     * @param mat
     *          the matrix to multiply this vector by
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d mul(Matrix4f mat, Vector3d dest) {
        if (this != dest) {
            dest.x = mat.m00 * x + mat.m10 * y + mat.m20 * z;
            dest.y = mat.m01 * x + mat.m11 * y + mat.m21 * z;
            dest.z = mat.m02 * x + mat.m12 * y + mat.m22 * z;
        } else {
            dest.set(mat.m00 * x + mat.m10 * y + mat.m20 * z,
                     mat.m01 * x + mat.m11 * y + mat.m21 * z,
                     mat.m02 * x + mat.m12 * y + mat.m22 * z);
        }
        return this;
    }

    /**
     * Multiply this Vector3d by the given matrix <code>mat</code>.
     * <p>
     * This method uses <tt>w=1.0</tt> as the fourth vector component.
     * 
     * @return this
     */
    public Vector3d mul(Matrix4f mat) {
        return mul(mat, this);
    }

    /**
     * Multiply this Vector3d by the given matrix <code>mat</code>.
     * 
     * @return this
     */
    public Vector3d mul(Matrix3f mat) {
        return mul(this, this);
    }

    /**
     * Multiply this Vector3d by the given matrix <code>mat</code>.
     * 
     * @return this
     */
    public Vector3d mul(Matrix3d mat) {
        return mul(mat, this);
    }

    /**
     * Multiply <code>this</code> by the given matrix <code>mat</code> and store the
     * result in <code>dest</code>.
     * 
     * @return this
     */
    public Vector3d mul(Matrix3d mat, Vector3d dest) {
        if (this != dest) {
            dest.x = mat.m00 * x + mat.m10 * y + mat.m20 * z;
            dest.y = mat.m01 * x + mat.m11 * y + mat.m21 * z;
            dest.z = mat.m02 * x + mat.m12 * y + mat.m22 * z;
        } else {
            dest.set(mat.m00 * x + mat.m10 * y + mat.m20 * z,
                     mat.m01 * x + mat.m11 * y + mat.m21 * z,
                     mat.m02 * x + mat.m12 * y + mat.m22 * z);
        }
        return this;
    }

    /**
     * Multiply <code>this</code> by the given matrix <code>mat</code> and store the
     * result in <code>dest</code>.
     */
    public Vector3d mul(Matrix3f mat, Vector3d dest) {
        if (this != dest) {
            dest.x = mat.m00 * x + mat.m10 * y + mat.m20 * z;
            dest.y = mat.m01 * x + mat.m11 * y + mat.m21 * z;
            dest.z = mat.m02 * x + mat.m12 * y + mat.m22 * z;
        } else {
            dest.set(mat.m00 * x + mat.m10 * y + mat.m20 * z,
                     mat.m01 * x + mat.m11 * y + mat.m21 * z,
                     mat.m02 * x + mat.m12 * y + mat.m22 * z);
        }
        return this;
    }

    /**
     * Multiply this Vector3d by the given scalar value.
     * 
     * @return this
     */
    public Vector3d mul(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * Multiply this Vector3d by the given scalar value and store the result in <code>dest</code>.
     * 
     * @param scalar
     *          the scalar factor
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d mul(double scalar, Vector3d dest) {
        dest.x = x * scalar;
        dest.y = y * scalar;
        dest.z = z * scalar;
        return this;
    }

    /**
     * Multiply <code>v</code> by the <code>scalar</code> value and store the result in <code>dest</code>.
     */
    public static void mul(Vector3f v, double scalar, Vector3d dest) {
        dest.x = v.x * scalar;
        dest.y = v.y * scalar;
        dest.z = v.z * scalar;
    }

    /**
     * Return the length squared of this vector.
     */
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Return the length of this vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalize this vector.
     * 
     * @return this
     */
    public Vector3d normalize() {
        double d = length();
        x /= d;
        y /= d;
        z /= d;
        return this;
    }
    
    /**
     * Normalize this vector and store the result in <code>dest</code>.
     * 
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d normalize(Vector3d dest) {
        double d = length();
        dest.x = x / d;
        dest.y = y / d;
        dest.z = z / d;
        return this;
    }

    /**
     * Set this vector to be the cross product of this and v2.
     * 
     * @param v
     *          the other vector
     * @return this
     */
    public Vector3d cross(Vector3d v) {
        set(y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x);
        return this;
    }

    /**
     * Calculate the cross product of this and v2 and store the result in <code>dest</code>.
     * 
     * @param v
     *          the other vector
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d cross(Vector3d v, Vector3d dest) {
        dest.set(y * v.z - z * v.y,
                 z * v.x - x * v.z,
                 x * v.y - y * v.x);
        return this;
    }
    
    /**
     * Return the distance between this vector and <code>v</code>.
     */
    public double distance(Vector3d v) {
        return Math.sqrt(
                  (v.x - this.x) * (v.x - this.x)
                + (v.y - this.y) * (v.y - this.y)
                + (v.z - this.z) * (v.z - this.z));
    }

    /**
     * Return the dot product of this vector and the supplied vector.
     */
    public double dot(Vector3d v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Return the cosine of the angle between <code>this</code> vector and
     * the supplied vector. Use this instead of <tt>Math.cos(angle(v))</tt>.
     * 
     * @see #angle(Vector3d)
     * 
     * @return the cosine of the angle
     */
    public double angleCos(Vector3d v) {
        double length1 = Math.sqrt((x * x) + (y * y) + (z * z));
        double length2 = Math.sqrt((v.x * v.x) + (v.y * v.y) + (v.z * v.z));
        double dot = (x * v.x) + (y * v.y) + (z * v.z);
        return dot / (length1 * length2);
    }

    /**
     * Return the angle between this vector and the supplied vector.
     * 
     * @see #angleCos(Vector3d)
     * 
     * @return the angle, in radians
     */
    public double angle(Vector3d v) {
        double cos = angleCos(v);
        // This is because sometimes cos goes above 1 or below -1 because of lost precision
        cos = Math.min(cos, 1);
        cos = Math.max(cos, -1);
        return Math.acos(cos);
    }

    /**
     * Set all components to zero.
     * 
     * @return this
     */
    public Vector3d zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        return this;
    }

    /**
     * Return a string representation of this vector.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<tt> 0.000E0;-</tt>".
     * 
     * @return the string representation
     */
    public String toString() {
        DecimalFormat formatter = new DecimalFormat(" 0.000E0;-");
        return toString(formatter).replaceAll("E(\\d+)", "E+$1");
    }

    /**
     * Return a string representation of this vector by formatting the vector components with the given {@link NumberFormat}.
     * 
     * @param formatter
     *          the {@link NumberFormat} used to format the vector components with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(x) + " " + formatter.format(y) + " " + formatter.format(z) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
    }

    /**
     * Negate this vector.
     * 
     * @return this
     */
    public Vector3d negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    /**
     * Negate this vector and store the result in <code>dest</code>.
     * 
     * @param dest
     *          will hold the result
     * @return this
     */
    public Vector3d negate(Vector3d dest) {
        dest.x = -x;
        dest.y = -y;
        dest.z = -z;
        return this;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vector3d other = (Vector3d) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
            return false;
        return true;
    }

    /**
     * Reflect this vector about the given normal vector.
     * 
     * @param normal
     *             the vector to reflect about
     * @return this
     */
    public Vector3d reflect(Vector3d normal) {
        double dot = this.dot(normal);
        x = x - 2.0 * dot * normal.x;
        y = y - 2.0 * dot * normal.y;
        z = z - 2.0 * dot * normal.z;
        return this;
    }

    /**
     * Reflect this vector about the given normal vector and store the result in <code>dest</code>.
     * 
     * @param normal
     *             the vector to reflect about
     * @param dest
     *             will hold the result
     * @return this
     */
    public Vector3d reflect(Vector3d normal, Vector3d dest) {
        double dot = this.dot(normal);
        dest.x = x - 2.0 * dot * normal.x;
        dest.y = y - 2.0 * dot * normal.y;
        dest.z = z - 2.0 * dot * normal.z;
        return this;
    }

    /**
     * Compute the half vector between this and the other vector.
     * 
     * @param other
     *             the other vector
     * @return this
     */
    public Vector3d half(Vector3d other) {
        return this.add(other).normalize();
    }

    /**
     * Compute the half vector between this and the other vector and store the result in <code>dest</code>.
     * 
     * @param other
     *             the other vector
     * @param dest
     *             will hold the result
     * @return this
     */
    public Vector3d half(Vector3d other, Vector3d dest) {
        dest.set(this).add(other).normalize();
        return this;
    }

    /**
     * Compute a smooth-step (i.e. hermite with zero tangents) interpolation
     * between <code>this</code> vector and the given vector <code>v</code> and
     * store the result in <code>dest</code>.
     * 
     * @param v
     *            the other vector
     * @param t
     *            the interpolation factor, within <tt>[0..1]</tt>
     * @param dest
     *            will hold the result
     * @return this
     */
    public Vector3d smoothStep(Vector3d v, double t, Vector3d dest) {
        dest.x = Interpolate.smoothStep(x, v.x, t);
        dest.y = Interpolate.smoothStep(y, v.y, t);
        dest.z = Interpolate.smoothStep(x, v.z, t);
        return this;
    }

    /**
     * Compute a hermite interpolation between <code>this</code> vector and its
     * associated tangent <code>t0</code> and the given vector <code>v</code>
     * with its tangent <code>t1</code> and store the result in
     * <code>dest</code>.
     * 
     * @param t0
     *            the tangent of <code>this</code> vector
     * @param v1
     *            the other vector
     * @param t1
     *            the tangent of the other vector
     * @param t
     *            the interpolation factor, within <tt>[0..1]</tt>
     * @param dest
     *            will hold the result
     */
    public Vector3d hermite(Vector3d t0, Vector3d v1, Vector3d t1, double t, Vector3d dest) {
        dest.x = Interpolate.hermite(x, t0.x, v1.x, t1.x, t);
        dest.y = Interpolate.hermite(y, t0.y, v1.y, t1.y, t);
        dest.z = Interpolate.hermite(z, t0.z, v1.z, t1.z, t);
        return this;
    }

}
