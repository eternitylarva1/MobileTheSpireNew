//SpriteBatch will use texture unit 0
uniform sampler2D u_texture;

//"in" varyings from our vertex shader
varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
	//sample the texture
	vec4 texColor = texture2D(u_texture, v_texCoord);
	
	//final color
	gl_FragColor = vec4(1.0,1.0,1.0,texColor.a);
}