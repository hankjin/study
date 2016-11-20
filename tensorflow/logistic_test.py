#!/bin/env python
import tensorflow as tf

learning_rate = 0.001
training_epochs = 30
batch_size = 4
display_step = 1

train_x = [
        [0,1,2],
        [0,1,3],
        [0,2,3],
        [0,3,4],
        [1,2,3],
        [1,2,4],
        [1,3,2],
        [1,4,3]
        ]
train_y = [
        [0,1],
        [0,1],
        [0,1],
        [0,1],
        [1,0],
        [1,0],
        [1,0],
        [1,0],
        ]
test_x = [
        [1,2,3],
        [0,2,3],
        [1,3,4],
        [0,3,4]
        ]
test_y = [
        [1,0],
        [0,1],
        [1,0],
        [0,1],
        ]
x = tf.placeholder(tf.float32, [None, 3])
y = tf.placeholder(tf.float32, [None, 2])

W = tf.Variable(tf.zeros([3, 2]))
b = tf.Variable(tf.zeros([2]))

pred = tf.nn.softmax(tf.matmul(x, W) + b)

cost = tf.reduce_mean(-tf.reduce_sum(y*tf.log(pred), reduction_indices=1))
optimizer = tf.train.GradientDescentOptimizer(learning_rate).minimize(cost)

init = tf.initialize_all_variables()

def get_batch(train_x, train_y, batch_idx):
    begin = batch_size * batch_idx
    end = batch_size * (batch_idx+1)
    if end > len(train_x):
        end = len(train_x)
    return train_x[begin:end], train_y[begin:end]

with tf.Session() as sess:
    sess.run(init)
    for epoch in range(training_epochs):
        avg_cost = 0
        total_batch = int(len(train_x)/batch_size)
        for i in range(total_batch):
            batch_xs, batch_ys = get_batch(train_x, train_y, i)
            _, c = sess.run([optimizer, cost], feed_dict = {x:batch_xs,
                y:batch_ys})
            avg_cost += c / total_batch
        if (epoch+1) % display_step == 0:
            print 'Epoch:', '%04d' % (epoch+1), 'cost=','{:.9f}'.format(avg_cost)
    print 'Optimization Finished'
    correct_prediction = tf.equal(tf.argmax(pred, 1), tf.argmax(y, 1))
    # Calculate accuracy for 3000 examples
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
    print "Accuracy:", accuracy.eval({x: train_x, y: train_y})
