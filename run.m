clc;
close all;
clear all;
compile;

jc = View3();
[~, hCanvas] = javacomponent(jc);
set(hCanvas, 'Units', 'normalized', 'Position', [0 0 1 1]);

% Now display something
load data.mat M;
verts = single(M.vertices);
faces = int32(M.faces)-1;
jc.add_mesh(M.vertices(:),M.faces(:));
