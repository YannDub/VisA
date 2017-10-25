import tensorflow as tf

b = tf.Variable([0], dtype=tf.float32)
w = tf.Variable([3], dtype=tf.float32)
x = tf.placeholder(tf.float32)

linear_model = w * x + b;

init = tf.global_variables_initializer()

session = tf.Session()

session.run(init)
print session.run(linear_model, {x: [1,2,3,4]})

y = tf.placeholder(tf.float32)
squared_deltas = tf.square(linear_model - y)
loss = tf.reduce_sum(squared_deltas)
print session.run(loss, {x:[1,2,3,4], y:[0,-1,-2,-3]})
