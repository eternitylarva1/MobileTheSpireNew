#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform float timedelta;
uniform vec2 sss;
uniform vec2 u_maxclamp;

void main()                                  
{                                            
  vec2 displacement = texture2D(u_texture2, v_texCoords/4.0).xy;
    //swap the x & y stuff here to make the waves vertical instead of horizontal
  float t=v_texCoords.y +displacement.y*0.7-0.15+  (sin(v_texCoords.x * 40.0+timedelta) * 0.003);
  gl_FragColor = v_color * texture2D(u_texture, vec2(v_texCoords.x,t ));
}