#version 330 core
layout(location = 0) out vec4 color;

in vec2 vTexCoord;
in vec4 vColor;

uniform sampler2D uTexImage;

void main() {
    vec4 texColor = texture(uTexImage, vTexCoord);
    color = texColor * vColor;
}
