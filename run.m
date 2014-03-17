clc;
close all;
clear all;
compile;

jc = View3();
[~, hCanvas] = javacomponent(jc);
set(hCanvas, 'Units', 'normalized', 'Position', [0 0 1 1]);



% load data
load sphere.mat M;

% data expexted as: [x1 y1 z1  x2 y2 z2 ...] 
verts = single(M.vertices)';
faces = (int32(M.faces)-1)';
jc.add_mesh(verts(:),faces(:));